package io.github.addressparse;

import io.github.addressparse.model.AddressResult;
import io.github.addressparse.model.AddressResult.Region;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * 中国收货地址智能解析
 * 支持解析：姓名、手机号、身份证号、邮编、省市区街道地址
 */
public class AddressParse {

    private static List<String[]> areaData;

    static {
        loadAreaData();
    }

    /**
     * 解析地址
     * @param address 原始地址字符串
     * @return 解析结果
     */
    public static AddressResult parse(String address) {
        return parse(address, null);
    }

    /**
     * 解析地址（支持高德地图API增强）
     * @param address 原始地址字符串
     * @param gdKey 高德地图API Key（可选）
     * @return 解析结果
     */
    public static AddressResult parse(String address, String gdKey) {
        AddressResult result = new AddressResult();
        
        // 解析用户信息
        Map<String, String> userDetail = parseUserDetail(address);
        result.setName(userDetail.get("name"));
        result.setMobile(userDetail.get("mobile"));
        result.setIdCard(userDetail.get("idCard"));
        result.setZipCode(userDetail.get("zipCode"));
        result.setAddress(userDetail.get("address"));

        // 解析地址详情
        parseAddressDetail(result, userDetail.get("address"), gdKey);

        return result;
    }

    private static Map<String, String> parseUserDetail(String address) {
        Map<String, String> detail = new HashMap<>();
        detail.put("name", "");
        detail.put("mobile", "");
        detail.put("idCard", "");
        detail.put("zipCode", "");

        // 1. 过滤常用说明字符
        address = address.replaceAll(
            "收货地址|地址|收货人|收件人|收货|邮编|电话|身份证号码|身份证号|身份证|详细地址|手机号码|所在地区|：|:|；|;|，|,|。|\\.|"|"|\"|\\'",
            " "
        );

        // 2. 合并空白字符
        address = address.replaceAll("\\s+", " ").trim();

        // 3. 去除手机号中的短横线
        address = address.replaceAll("0-|0?(\\d{3})-(\\d{4})-(\\d{4})", "$1$2$3");

        // 4. 提取身份证号
        Pattern idCardPattern = Pattern.compile("\\d{18}|\\d{17}[Xx]");
        Matcher idCardMatcher = idCardPattern.matcher(address);
        if (idCardMatcher.find()) {
            detail.put("idCard", idCardMatcher.group().toUpperCase());
            address = address.replace(idCardMatcher.group(), "");
        }

        // 5. 提取手机号或座机号
        Pattern mobilePattern = Pattern.compile("\\d{7,11}|\\d{3,4}-\\d{6,8}");
        Matcher mobileMatcher = mobilePattern.matcher(address);
        if (mobileMatcher.find()) {
            detail.put("mobile", mobileMatcher.group());
            address = address.replace(mobileMatcher.group(), "");
        }

        // 6. 提取邮编
        Pattern zipPattern = Pattern.compile("\\d{6}");
        Matcher zipMatcher = zipPattern.matcher(address);
        if (zipMatcher.find()) {
            detail.put("zipCode", zipMatcher.group());
            address = address.replace(zipMatcher.group(), "");
        }

        // 合并空格并分割
        address = address.replaceAll(" {2,}", " ").trim();
        String[] parts = address.split(" ");
        
        if (parts.length > 1) {
            String name = parts[0];
            for (String part : parts) {
                if (part.length() < name.length()) {
                    name = part;
                }
            }
            detail.put("name", name);
            address = address.replace(name, "").trim();
        }
        
        detail.put("address", address);
        return detail;
    }


    private static void parseAddressDetail(AddressResult result, String address, String gdKey) {
        result.setProvince(new Region("", ""));
        result.setCity(new Region("", ""));
        result.setDistrict(new Region("", ""));
        result.setFormattedAddress("");

        if (address == null || address.isEmpty()) return;

        address = address.replace("_", "");
        String formattedAddress = address.replaceFirst("^(\\D+?)(市)", "");
        formattedAddress = formattedAddress.replaceFirst("^(\\D+?)(区|县|旗)", "");

        List<String[]> matched = new ArrayList<>();

        // 匹配三级地址
        for (String[] area : areaData) {
            String districtName = area[2].substring(7);
            String districtShort = districtName.length() > 1 ? 
                districtName.substring(0, districtName.length() - 1) : districtName;
            if (address.contains(districtShort)) {
                matched.add(area);
            }
        }

        // 二级地址过滤
        if (matched.size() > 1) {
            List<String[]> filtered = new ArrayList<>();
            for (String[] area : matched) {
                String cityName = area[1].substring(7);
                String cityShort = cityName.length() > 1 ? 
                    cityName.substring(0, cityName.length() - 1) : cityName;
                if (address.contains(cityShort)) {
                    filtered.add(area);
                }
            }
            if (!filtered.isEmpty()) matched = filtered;
        }

        // 一级地址过滤
        if (matched.size() > 1) {
            List<String[]> filtered = new ArrayList<>();
            for (String[] area : matched) {
                String provinceName = area[0].substring(7);
                String provinceShort = provinceName.length() > 1 ? 
                    provinceName.substring(0, provinceName.length() - 1) : provinceName;
                if (address.contains(provinceShort)) {
                    filtered.add(area);
                }
            }
            if (!filtered.isEmpty()) matched = filtered;
        }

        // 完整名称再次匹配
        if (matched.size() > 1) {
            List<String[]> filtered = new ArrayList<>();
            for (String[] area : matched) {
                String districtName = area[2].substring(7);
                if (address.contains(districtName)) {
                    filtered.add(area);
                }
            }
            if (!filtered.isEmpty()) matched = filtered;
        }

        if (!matched.isEmpty()) {
            String[] area = matched.get(0);
            result.setProvince(new Region(area[0].substring(0, 6), area[0].substring(7)));
            result.setCity(new Region(area[1].substring(0, 6), area[1].substring(7)));
            result.setDistrict(new Region(area[2].substring(0, 6), area[2].substring(7)));
            result.setFormattedAddress(formattedAddress.trim());
        }

        // 高德地图API增强
        if (gdKey != null && !gdKey.isEmpty()) {
            enhanceWithGaode(result, address, formattedAddress, gdKey);
        }
    }

    private static void enhanceWithGaode(AddressResult result, String address, String formattedAddress, String gdKey) {
        try {
            String urlStr = "https://restapi.amap.com/v3/geocode/geo?key=" + gdKey + "&address=" + 
                java.net.URLEncoder.encode(address, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String response = reader.lines().collect(Collectors.joining());
                    parseGaodeResponse(result, response, formattedAddress);
                }
            }
        } catch (Exception e) {
            // 忽略API调用失败
        }
    }

    private static void parseGaodeResponse(AddressResult result, String json, String formattedAddress) {
        // 简单JSON解析
        if (json.contains("\"geocodes\":[]")) return;
        
        try {
            int adcodeStart = json.indexOf("\"adcode\":\"") + 10;
            int adcodeEnd = json.indexOf("\"", adcodeStart);
            String adcode = json.substring(adcodeStart, adcodeEnd);

            int provinceStart = json.indexOf("\"province\":\"") + 12;
            int provinceEnd = json.indexOf("\"", provinceStart);
            String province = json.substring(provinceStart, provinceEnd);

            String provinceCode = result.getProvince().getCode();
            if (provinceCode.isEmpty() || !provinceCode.equals(adcode.substring(0, 2) + "0000")) {
                result.setProvince(new Region(adcode.substring(0, 2) + "0000", province));
                
                // 解析city
                int cityStart = json.indexOf("\"city\":\"") + 8;
                int cityEnd = json.indexOf("\"", cityStart);
                String city = json.substring(cityStart, cityEnd);
                if (!city.equals("[]")) {
                    result.setCity(new Region(adcode.substring(0, 4) + "00", city));
                }

                // 解析district
                int districtStart = json.indexOf("\"district\":\"") + 12;
                int districtEnd = json.indexOf("\"", districtStart);
                String district = json.substring(districtStart, districtEnd);
                if (!district.equals("[]")) {
                    result.setDistrict(new Region(adcode, district));
                }
            }

            if (formattedAddress == null || formattedAddress.isEmpty()) {
                int faStart = json.indexOf("\"formatted_address\":\"") + 21;
                int faEnd = json.indexOf("\"", faStart);
                result.setFormattedAddress(json.substring(faStart, faEnd));
            } else {
                result.setFormattedAddress(formattedAddress);
            }
        } catch (Exception e) {
            // 解析失败忽略
        }
    }

    private static void loadAreaData() {
        areaData = new ArrayList<>();
        try (InputStream is = AddressParse.class.getResourceAsStream("/area.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    areaData.add(parts);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load area data", e);
        }
    }
}

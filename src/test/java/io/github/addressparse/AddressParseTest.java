package io.github.addressparse;

import io.github.addressparse.model.AddressResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressParseTest {

    @Test
    void testParseFullAddress() {
        String address = "身份证号：51250119910927226x 收货地址张三收货地址：成都市武侯区美领馆路11号附2号 617000 136-3333-6666";
        AddressResult result = AddressParse.parse(address);

        assertEquals("张三", result.getName());
        assertEquals("13633336666", result.getMobile());
        assertEquals("51250119910927226X", result.getIdCard());
        assertEquals("617000", result.getZipCode());
        assertEquals("四川省", result.getProvince().getName());
        assertEquals("成都市", result.getCity().getName());
        assertEquals("武侯区", result.getDistrict().getName());
    }

    @Test
    void testParseSimpleAddress() {
        String address = "北京市朝阳区建国路88号 李四 13812345678";
        AddressResult result = AddressParse.parse(address);

        assertEquals("李四", result.getName());
        assertEquals("13812345678", result.getMobile());
        assertEquals("北京市", result.getProvince().getName());
        assertEquals("朝阳区", result.getDistrict().getName());
    }

    @Test
    void testParseMobileWithDash() {
        String address = "王五 138-1234-5678 上海市浦东新区";
        AddressResult result = AddressParse.parse(address);

        assertEquals("王五", result.getName());
        assertEquals("13812345678", result.getMobile());
    }

    @Test
    void testParseIdCard() {
        String address = "身份证号码110101199001011234 张三 北京市东城区";
        AddressResult result = AddressParse.parse(address);

        assertEquals("110101199001011234", result.getIdCard());
    }
}

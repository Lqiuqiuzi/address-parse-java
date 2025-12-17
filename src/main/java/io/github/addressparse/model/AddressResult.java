package io.github.addressparse.model;

/**
 * 地址解析结果
 */
public class AddressResult {
    private String name;
    private String mobile;
    private String idCard;
    private String zipCode;
    private String address;
    private Region province;
    private Region city;
    private Region district;
    private String formattedAddress;

    public static class Region {
        private String code;
        private String name;

        public Region() {}

        public Region(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() {
            return "Region{code='" + code + "', name='" + name + "'}";
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Region getProvince() { return province; }
    public void setProvince(Region province) { this.province = province; }
    public Region getCity() { return city; }
    public void setCity(Region city) { this.city = city; }
    public Region getDistrict() { return district; }
    public void setDistrict(Region district) { this.district = district; }
    public String getFormattedAddress() { return formattedAddress; }
    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }

    @Override
    public String toString() {
        return "AddressResult{name='" + name + "', mobile='" + mobile + "', idCard='" + idCard +
               "', zipCode='" + zipCode + "', province=" + province + ", city=" + city +
               ", district=" + district + ", formattedAddress='" + formattedAddress + "'}";
    }
}

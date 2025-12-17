# Address Parse Java

中国收货地址智能解析 Java 版本

将连在一起的收货信息解析成单个信息，方便录入系统。解析成功率保持在 99% 以上。

## 功能特性

- 解析姓名
- 解析手机号码/座机号码
- 解析身份证号码
- 解析邮政编码
- 解析省市区三级地址
- 支持高德地图 API 增强解析精度（可选）

## 安装

### Maven

```xml
<dependency>
    <groupId>io.github.Lqiuqiuzi</groupId>
    <artifactId>address-parse</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.Lqiuqiuzi:address-parse:1.0.0'
```

## 使用方法

### 基本用法

```java
import io.github.addressparse.AddressParse;
import io.github.addressparse.model.AddressResult;

String address = "身份证号：51250119910927226x 收货地址张三收货地址：成都市武侯区美领馆路11号附2号 617000 136-3333-6666";
AddressResult result = AddressParse.parse(address);

System.out.println("姓名: " + result.getName());
System.out.println("手机: " + result.getMobile());
System.out.println("身份证: " + result.getIdCard());
System.out.println("邮编: " + result.getZipCode());
System.out.println("省份: " + result.getProvince().getName());
System.out.println("城市: " + result.getCity().getName());
System.out.println("区县: " + result.getDistrict().getName());
System.out.println("详细地址: " + result.getFormattedAddress());
```

### 使用高德地图 API 增强

```java
String gdKey = "your_gaode_api_key";
AddressResult result = AddressParse.parse(address, gdKey);
```

### 返回结果示例

```
姓名: 张三
手机: 13633336666
身份证: 51250119910927226X
邮编: 617000
省份: 四川省
城市: 成都市
区县: 武侯区
详细地址: 美领馆路11号附2号
```

## 构建

```bash
mvn clean package
```

## 许可证

MIT License

## 致谢

本项目基于 [address-parse](https://github.com/hwj911327/address-parse) PHP 版本移植。

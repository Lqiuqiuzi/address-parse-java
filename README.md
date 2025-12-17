# Address Parse Java

[![](https://jitpack.io/v/Lqiuqiuzi/address-parse-java.svg)](https://jitpack.io/#Lqiuqiuzi/address-parse-java)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://www.java.com)

中国收货地址智能解析 Java 版本

将连在一起的收货信息解析成单个信息，方便录入系统。解析成功率保持在 99% 以上。

## 功能特性

- 解析姓名
- 解析手机号码/座机号码
- 解析身份证号码
- 解析邮政编码
- 解析省市区三级地址
- 支持高德地图 API 增强解析精度（可选）
- 支持 Java 8+

## 在线演示

👉 [https://lqiuqiuzi.github.io/address-parse-vue/](https://lqiuqiuzi.github.io/address-parse-vue/)

## 安装

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Lqiuqiuzi</groupId>
        <artifactId>address-parse-java</artifactId>
        <version>v1.0.4</version>
    </dependency>
</dependencies>
```

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Lqiuqiuzi:address-parse-java:v1.0.4'
}
```

## 使用方法

### 基本用法

```java
import io.github.addressparse.AddressParse;
import io.github.addressparse.model.AddressResult;

String address = "张三 13812345678 北京市朝阳区建国路88号 100022";
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
手机: 13812345678
身份证: 
邮编: 100022
省份: 北京市
城市: 北京城区
区县: 朝阳区
详细地址: 建国路88号
```

## 支持的输入格式

支持空格、逗号、回车等多种分隔符：

```
张三 13812345678 北京市朝阳区xxx
张三,13812345678,北京市朝阳区xxx
张三，13812345678，北京市朝阳区xxx
张三
13812345678
北京市朝阳区xxx
```

## 许可证

[MIT License](LICENSE)

## 相关项目

- [address-parse-vue](https://github.com/Lqiuqiuzi/address-parse-vue) - vue 版本

## 联系方式

**企鹅** 2177273545
**卫星** l3885236283

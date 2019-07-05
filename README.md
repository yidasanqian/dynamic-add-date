# dynamic-add-date
[![Build Status](https://travis-ci.org/yidasanqian/dynamic-add-date.svg?branch=master)](https://travis-ci.org/yidasanqian/dynamic-add-date) 
[![Maven Central](https://img.shields.io/badge/maven--central-1.0.4-blue.svg)](http://search.maven.org/#artifactdetails%7Cio.github.yidasanqian%7Cdynamic-add-date%7C1.0.4%7Cjar)
[![LICENSE](https://img.shields.io/badge/license-NPL%20(The%20996%20Prohibited%20License)-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)

Dynamic-add-date是基于Mybatis插件原理开发的可以动态在`Insert`和`Update` Sql语句中添加日期列和对应的值的插件。 

## 要求
- Maven
- 支持jdk7及之后的版本
- MySql

## 功能
- 自定义要生成的日期列的名称
- 自动处理原始Sql语句中已包含自定义日期列名
- 支持批量插入、批量更新Sql日期列的生成
- 支持忽略表，表名支持正则表达式

## 在你的应用中添加Dynamic-add-date
添加下面的依赖到你的pom文件中：
```
<dependency>
    <groupId>io.github.yidasanqian</groupId>
    <artifactId>dynamic-add-date</artifactId>
    <version>1.0.4</version>
</dependency>
```

若使用Gradle/Grails:
```
compile 'io.github.yidasanqian:dynamic-add-date:1.0.4'
```


然后在`mybatis-config.xml`配置文件中加入如下设置即可：
```
<plugins>
        <plugin interceptor="io.github.yidasanqian.AddDateInterceptor">
        </plugin>
</plugins>
```

例如，原始Sql为：
 ```
 insert into user(name, profession) values(?, ?)
 ```

使用该插件后Sql语句为：
 ```
 insert into user(name, profession, gmt_create, gmt_modified) values(?, ?, '2017-10-15 10:10:10', '2017-10-15 10:10:10')
 ```
 
 批量插入的情况：
 ```
 insert into user(name, profession) values(?, ?), (?, ?), (?, ?)
 ```
 
 使用该插件后Sql语句为：
 ```
 insert into user(name, profession, gmt_create, gmt_modified) values(?, ?, '2017-10-15 10:10:10', '2017-10-15 10:10:10'),
 (?, ?, '2017-10-15 10:10:10', '2017-10-15 10:10:10'), (?, ?, '2017-10-15 10:10:10', '2017-10-15 10:10:10')
 ```
 
 默认新建日期的列名为 `gmt_create`, 更新日期的列名为 `gmt_modified`。
 
 可以通过设置key `createDateColumnName`和 `updateDateColumnName`来分别指定日期列的名称：
 ```
 <plugin interceptor="io.github.yidasanqian.AddDateInterceptor">
     <property name="createDateColumnName" value="gmt_create"/>
     <property name="updateDateColumnName" value="gmt_modified"/>
 </plugin>
 ```
 
## 与Spring Boot集成
首先mybatis依赖
```
<!-- https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.1</version>
</dependency>
```
其次写入自定义配置：

1）xml方式进行配置  
也是在`mybatis-config.xml`配置文件中加入如下设置：
```
 <plugin interceptor="io.github.yidasanqian.AddDateInterceptor">
     <property name="createDateColumnName" value="gmt_create"/>
     <property name="updateDateColumnName" value="gmt_modified"/>
 </plugin>
```
然后在 `application.properties`加入如下配置即可：
```
mybatis.config-location=classpath:mybatis-config.xml
```

2）创建bean方式进行配置 
```
@Bean
public AddDateInterceptor addDateInterceptor(){
    Properties properties = new Properties();
    properties.setProperty("createDateColumnName","gmt_create");
    properties.setProperty("updateDateColumnName","gmt_modified");
    AddDateInterceptor addDateInterceptor = new AddDateInterceptor();
    addDateInterceptor.setProperties(properties);
    return addDateInterceptor;
}
```
## 忽略表
实际应用中并不是所有的表都需要创建时间和更新时间字段，如何设置忽略处理的表呢？

1）xml方式  
也是在`mybatis-config.xml`配置文件中加入如下设置：
```
<plugins>
    <plugin interceptor="io.github.yidasanqian.AddDateInterceptor">
        <property name="ignoreTables" value="^user.*, permission"/>
    </plugin>
</plugins>
```    

2）创建Bean方式 
```
@Bean
public AddDateInterceptor addDateInterceptor(){
    Properties properties = new Properties();
    properties.setProperty("createDateColumnName","gmt_create");
    properties.setProperty("updateDateColumnName","gmt_modified");
    properties.setProperty("ignoreTables","^user.*, permission");
    AddDateInterceptor addDateInterceptor = new AddDateInterceptor();
    addDateInterceptor.setProperties(properties);
    return addDateInterceptor;
}
```
其中name=`ignoreTables`属性值为固定，不能变，value的格式：`表名, 表名`。

其中value的值为表名，支持正则表达式，且多个表名以英文逗号`,`分隔。
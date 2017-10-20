# dynamic-add-date
[![Build Status](https://travis-ci.org/yidasanqian/dynamic-add-date.svg?branch=master)](https://travis-ci.org/yidasanqian/dynamic-add-date) 
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/badge/maven--central-1.0.3-blue.svg)](http://search.maven.org/#artifactdetails%7Cio.github.yidasanqian%7Cdynamic-add-date%7C1.0.3%7Cjar)

Dynamic-add-date是基于Mybatis插件原理开发的可以动态在`Insert`和`Update` Sql语句中添加日期列和对应的值的插件。 

## 要求
- Maven
- 支持jdk7及之后的版本
- MySql

## 功能
- 自定义要生成的日期列的名称
- 自动处理原始Sql语句中已包含自定义日期列名
- 支持批量插入、批量更新Sql日期列的生成

## 在你的应用中添加Dynamic-add-date
添加下面的依赖到你的pom文件中：
```
<dependency>
    <groupId>io.github.yidasanqian</groupId>
    <artifactId>dynamic-add-date</artifactId>
    <version>1.0.3</version>
</dependency>
```

若使用Gradle/Grails:
```
compile 'io.github.yidasanqian:dynamic-add-date:1.0.3'
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


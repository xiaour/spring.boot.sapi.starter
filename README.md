# spring.boot.sapi.starter
一个简单的API输出工具，只需一行配置，即可将接口中的所有API接口及参数全部以结构化的方式输出的页面上。
基于SpringMvc或Springboot的接口管理工具

1.在工程pom.xml中引入

<dependency>
  <groupId>com.github.xiaour</groupId>
  <artifactId>spring.boot.sapi.starter</artifactId>
</dependency>

2.在工程配置文件 application.yml中加入

spring:
   api:
     base:
       pack:
        - your.controller.package1
        - your.controller.package2
 
 3.在浏览器中打开 http://127.0.0.1:{port}/{context-path}/sapi 就可以看到你的API接口了
 
 ![avatar](https://raw.githubusercontent.com/xiaour/xiaour.github.com/master/WechatIMG2152.jpeg)


## 版本功能

当前为1.0版本，实现了输出接口URL和参数；
2.0版本将实现添加接口说明、历史版本接口管理、接口测试页面

-------------------------------------------------------------
更新日志
20180621.页面上提供了POST和GET测试。

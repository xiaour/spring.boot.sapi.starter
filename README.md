[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.xiaour/xiaour.springboot.sapi.starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.xiaour%22%20AND%20a:%22xiaour.springboot.sapi.starter%22)

# spring.boot.sapi.starter
一个简单的API输出工具，只需简单配置，即可将接口中的所有API接口及参数全部以结构化的方式展示，并提供了接口参数测试页面。
基于Springboot2.x 的接口管理工具

## 快速开始 Quick start

### 1.远程仓库配置
1.1 Maven
```xml
<dependency>
<groupId>com.github.xiaour</groupId>
<artifactId>xiaour.springboot.sapi.starter</artifactId>
<version>1.4</version>
</dependency>
```
1.2 Gradle

```gradle
compile 'com.github.xiaour:xiaour.springboot.sapi.starter:1.4'
```
1.3 [更多仓库配置类型请参考 >>>](https://search.maven.org/artifact/com.github.xiaour/xiaour.springboot.sapi.starter/1.4/jar)


### 2.启动类加入注解@Sapi.
controllers属性可以声明多个，如controllers = {"com.example.demo.ctrl","com.example.demo2.ctrl"},controllers的路径对应项目中controller所在的路径。enable是SAPI的开关，如果需要上生产环境只需要enable=false就可以了，这样避免在生产暴露接口。

```java
@Sapi(controllers = {"com.example.demo.ctrl"})
@SpringBootApplication
public class DemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(DemoApplication.class, args);
   }
}
```
### 3.Controller类加入注解@SapiGroup(title="").
SapiGroup是用于分组输出的注解，可以在上面加入中文接口注释，该注解可有可无，如不注解，默认输出的是当前类名。
```java
@SapiGroup(title = "接口分组名称")
@RestController
@RequestMapping("/cat")
public class CatController {
    
}
```
 
 ### 4.启动应用控制台将输出SAPI的访问链接，点击链接即可立即开始调试接口。
```xml
2018-12-08 21:54:49.328  INFO 1112 --- [  restartedMain] c.g.x.a.config.ApiServerAutoConfigure    : SAPI page url:http://127.0.0.1:8080/demo/sapi
```
 
 ![avatar](https://oscimg.oschina.net/oscnet/1a3545fa7abcfff02354740d61ce81daf56.jpg)


## 版本功能
```text
当前为1.4版本，实现了输出接口URL和参数，分组输出接口；
2.0版本将实现添加接口说明、历史版本接口管理
```
-------------------------------------------------------------
## 更新日志

20180621.页面上提供了POST和GET测试。

20180628.新增支持文件类型的数据。

20180702.新增支持自定义请求Header。

20180710.新增了接口分页。

20180828.优化了返回结果格式化的问题。

20180907.改版了界面列表展示部分，修复了Class扫描不到的情况。

20180912.增加了对RequestBody的支持,支持MultipartFile上传文件。

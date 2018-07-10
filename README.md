# spring.boot.sapi.starter
一个简单的API输出工具，只需简单配置，即可将接口中的所有API接口及参数全部以结构化的方式输出的页面上。
基于SpringMvc或Springboot的接口管理工具

1.下载并打包Jar配置好POM
```xml
<dependency>
   <groupId>com.github.xiaour</groupId>
   <artifactId>xiaour.springboot.sapi.starter</artifactId>
   <version>1.0</version>
   <scope>system</scope>
   <systemPath>${project.basedir}/libs/xiaour.springboot.sapi.starter-1.0.jar</systemPath>
</dependency>
```
2.启动类加入注解Sapi和ServletComponentScan

```java
@Sapi(controllers = {"com.example.demo.ctrl","com.example.demo.ctrl2"})
@ServletComponentScan(basePackages = {"com.github.xiaour.api_scanner.servlet"})
@ComponentScan
@SpringBootApplication
public class DemoApplication {

   public static void main(String[] args) {
      SpringApplication.run(DemoApplication.class, args);
   }
}
```
 
 3.在浏览器中打开 http://127.0.0.1:{port}/{context-path}/sapi 就可以看到你的API接口了
 
 ![avatar](https://oscimg.oschina.net/oscnet/bfb862085fc470b4cea467550ddf3b3d3f9.jpg)


## 版本功能

当前为1.0版本，实现了输出接口URL和参数；
2.0版本将实现添加接口说明、历史版本接口管理、接口测试页面
-------------------------------------------------------------
更新日志
20180621.页面上提供了POST和GET测试。
20180628.新增支持文件类型的数据。
20180710.新增了接口分页。

[![Chat at https://gitter.im/spring-sapi/](https://badges.gitter.im/javalin-io/general.svg)](https://gitter.im/spring-sapi/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven](https://img.shields.io/badge/maven-v1.2-blue.svg)](https://search.maven.org/artifact/com.github.xiaour/xiaour.springboot.sapi.starter/1.2/jar)

# spring.boot.sapi.starter
一个简单的API输出工具，只需简单配置，即可将接口中的所有API接口及参数全部以结构化的方式输出的页面上。
基于SpringMvc或Springboot的接口管理工具

1.下载并打包Jar配置好POM
```xml
<dependency>
  <groupId>com.github.xiaour</groupId>
  <artifactId>xiaour.springboot.sapi.starter</artifactId>
  <version>1.2</version>
</dependency>
```
2.启动类加入注解Sapi.
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
 
 3.在浏览器中打开 http://127.0.0.1:{port}/{context-path}/sapi 就可以看到你的API接口了
 
 ![avatar](https://oscimg.oschina.net/oscnet/bfb862085fc470b4cea467550ddf3b3d3f9.jpg)


## 版本功能

当前为1.2版本，实现了输出接口URL和参数；
2.0版本将实现添加接口说明、历史版本接口管理、接口测试页面
-------------------------------------------------------------
更新日志

20180621.页面上提供了POST和GET测试。

20180628.新增支持文件类型的数据。

20180702.新增支持自定义请求Header。

20180710.新增了接口分页。

20180828.优化了返回结果格式化的问题。

20180907.改版了界面列表展示部分，修复了Class扫描不到的情况。（该版本尚未发布到Maven Center，请自行Build）

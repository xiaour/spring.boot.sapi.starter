package com.github.xiaour.api_scanner.config;

import com.github.xiaour.api_scanner.filter.ApiJsonServlet;
import com.github.xiaour.api_scanner.filter.ApiServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/6/4 10:20
 */

@Configuration
public class ServletConf {


    /**
     * 代码注册servlet(不需要@ServletComponentScan注解)
     * @return
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new ApiServlet(), "/sapi");// ServletName默认值为首字母小写，即myServlet1
    }

    /**
     * 代码注册servlet(不需要@ServletComponentScan注解)
     * @return
     */
    @Bean
    public ServletRegistrationBean servletRegistrationApiJson() {
        return new ServletRegistrationBean(new ApiJsonServlet(), "/apiList");// ServletName默认值为首字母小写，即myServlet1
    }


}

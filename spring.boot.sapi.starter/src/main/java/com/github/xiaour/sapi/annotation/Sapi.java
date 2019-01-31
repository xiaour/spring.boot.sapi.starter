package com.github.xiaour.sapi.annotation;

import org.springframework.boot.web.servlet.ServletComponentScan;

import java.lang.annotation.*;

/**
 * @Author: zhangtao@suiyueyule.com
 * @Date: 2019-01-31 17:27
 * @version: v1.0
 * @Description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ServletComponentScan(basePackages = {"com.github.xiaour.sapi.servlet"})
public @interface Sapi {

    String [] controllers() default  {""};

    boolean enable() default true;
}


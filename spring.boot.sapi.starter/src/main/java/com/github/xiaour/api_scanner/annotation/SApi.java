package com.github.xiaour.api_scanner.annotation;

import org.springframework.boot.web.servlet.ServletComponentScan;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ServletComponentScan(basePackages = {"com.github.xiaour.api_scanner.servlet"})
public @interface Sapi {

    String [] controllers() default  {""};

    boolean enable() default true;
}

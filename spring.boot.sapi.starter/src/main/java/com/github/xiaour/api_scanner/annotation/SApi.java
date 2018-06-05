package com.github.xiaour.api_scanner.annotation;

import java.lang.annotation.*;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/6/4 10:56
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SApi {

    String userName() default  "";
}

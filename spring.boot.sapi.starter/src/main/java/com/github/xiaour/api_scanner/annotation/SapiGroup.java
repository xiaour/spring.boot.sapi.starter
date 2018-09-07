package com.github.xiaour.api_scanner.annotation;

import java.lang.annotation.*;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/9/4 11:25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SapiGroup {
    String title();
}

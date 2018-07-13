package com.github.xiaour.api_scanner.annotation;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Sapi {

    String [] controllers() default  {""};
}

package com.k2archer.server.tomato.intercepter.request;


import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {
    int second() default 1;
    int maximum() default 1;
}

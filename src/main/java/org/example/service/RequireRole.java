package org.example.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//角色校验注解：标注在Controller方法或类上，拦截器据此校验当前登录用户角色
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    //允许访问的角色列表
    String[] value() default {};

    //true: 必须同时具备所有角色；false(默认): 具备其一即可
    boolean requireAll() default false;
}

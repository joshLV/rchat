package com.rchat.platform.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 没有安全设置，需要特殊指定出来的业务或者方法
 * 
 * @author dzhang
 * @since 2017-02-25 18:47:25
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface NoSecurity {

}

package com.rchat.platform.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 安全操作
 *
 * @author dzhang
 * @since 2017-02-24 16:44:08
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityMethod {
	/**
	 * 要操作的资源
	 * 
	 * @return
	 */
	ResourceType resource() default ResourceType.UNKNOW;

	/**
	 * 是否保护
	 * 
	 * @return
	 */
	@AliasFor("value")
	boolean secured() default true;

	/**
	 * 要执行的权限类型，默认只有查询权限
	 * 
	 * @return
	 */
	OperationType operation() default OperationType.NONE;

	@AliasFor("secured")
	boolean value() default true;
}

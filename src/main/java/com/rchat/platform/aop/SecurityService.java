/**
 * 
 */
package com.rchat.platform.aop;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * 安全服务，一个安全服务只能控制一种资源的访问权限
 *
 * @author dzhang
 * @since 2017-02-25 18:43:48
 */
public @interface SecurityService {
	/**
	 * 安全资源类型
	 * 
	 * @return
	 */
	ResourceType value();
}

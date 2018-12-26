package com.rchat.platform.common;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface LogAPI {
	@AliasFor("value")
	String operation() default "";

	String resource() default "";

	@AliasFor("operation")
	String value() default "";

}

package com.cat2bug.junit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记随机参数的注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomParameter {
	/**
	 * 类名称
	 * @return 类名称
	 */
	String className() default "";
	/**
	 * 方法名
	 * @return 方法名
	 */
	String methodName() default "";
	/**
	 * 参数名
	 * @return 参数名
	 */
	String parameterName() default "";
}

package com.cat2bug.junit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记随机参数的注解
 * @author yuzhantao
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomParameter {
	/**
	 * 类名称
	 * @return
	 */
	String className() default "";
	/**
	 * 方法名
	 * @return
	 */
	String methodName() default "";
	/**
	 * 参数名
	 * @return
	 */
	String parameterName() default "";
}

package com.cat2bug.junit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PushDefect {
	/**
	 * 提交接口的系统地址
	 * @return
	 */
	String host() default "http://www.cat2bug.com/cloud/";
	/**
	 * 项目的密钥
	 * @return
	 */
	String projectKey() default "";
	/**
	 * 处理人账号
	 * @return
	 */
	String handler() default "";
	/**
	 * 是否推送测试报告
	 * @return
	 */
	boolean isPush() default true;
}

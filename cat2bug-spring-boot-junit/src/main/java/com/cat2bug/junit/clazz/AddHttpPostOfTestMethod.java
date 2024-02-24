package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 添加Http post协议测试方法
 * @author yuzhantao
 *
 */
public class AddHttpPostOfTestMethod extends AbstractAddHttpAPIOfTestMethod {

	public AddHttpPostOfTestMethod(ITestClassFactory factory, String name, Method srcMethod, Class<?>[] paramesClasses,
			Map<Class<? extends Annotation>, Map<String, Object>> annoations) {
		super(factory, name, srcMethod, paramesClasses, annoations);
	}

	@Override
	public String getTestHttpMethodName() {
		return "testPost";
	}
	
}

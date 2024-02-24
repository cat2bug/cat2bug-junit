package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public class AddHttpPutOfTestMethod  extends AbstractAddHttpAPIOfTestMethod {

	public AddHttpPutOfTestMethod(ITestClassFactory factory, String name, Method srcMethod, Class<?>[] paramesClasses,
			Map<Class<? extends Annotation>, Map<String, Object>> annoations) {
		super(factory, name, srcMethod, paramesClasses, annoations);
	}

	@Override
	public String getTestHttpMethodName() {
		return "testPut";
	}
}

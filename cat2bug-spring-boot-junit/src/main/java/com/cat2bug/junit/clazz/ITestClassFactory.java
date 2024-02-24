package com.cat2bug.junit.clazz;

import javassist.CtClass;

public interface ITestClassFactory {
	CtClass createTestClass(Class<?> clazz) throws Exception;
}

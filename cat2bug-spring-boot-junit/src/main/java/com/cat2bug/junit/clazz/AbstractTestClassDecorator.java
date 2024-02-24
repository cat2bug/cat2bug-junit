package com.cat2bug.junit.clazz;

import javassist.CtClass;

/**
 * 抽象装饰模式测试类
 * 
 * @author yuzhantao
 *
 */
public class AbstractTestClassDecorator implements ITestClassFactory {

	private ITestClassFactory testClassFactory;

	public AbstractTestClassDecorator(ITestClassFactory factory) {
		this.testClassFactory = factory;
	}

	@Override
	public CtClass createTestClass(Class<?> clazz) throws Exception {
		return this.testClassFactory.createTestClass(clazz);
	}
}

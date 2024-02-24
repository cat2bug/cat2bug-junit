package com.cat2bug.junit.clazz;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * 测试类工厂
 * @author yuzhantao
 *
 */
public class TestClassFactory implements ITestClassFactory {
	String className;
	String packageName;
	public TestClassFactory(String className,String packageName) {
		this.className=className;
		this.packageName=packageName;
	}
	/**
	 * 创建测试类
	 */
	@Override
	public CtClass createTestClass(Class<?> clazz) throws Exception {
		String longClassName = this.packageName + "." + this.className;
		ClassPool cp = ClassPool.getDefault();
		CtClass ctClass = cp.makeClass(longClassName);
		return ctClass;
	}

}

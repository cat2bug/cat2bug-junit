package com.cat2bug.junit.clazz;

/**
 * 创建一个没有内容的构造函数
 * @author yuzhantao
 *
 */
public class AddConstructorOfTestClass extends AbstractAddConstructorOfTestClass {

	public AddConstructorOfTestClass(ITestClassFactory factory) {
		super(factory);
	}

	@Override
	public String body() {
		return "{}";
	}

}

package com.cat2bug.junit.clazz;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.Modifier;

/**
 * 构建测试类的构造函数
 * 
 * @author yuzhantao
 *
 */
public abstract class AbstractAddConstructorOfTestClass extends AbstractTestClassDecorator {
	private Class<?>[] paramesClasses;

	public AbstractAddConstructorOfTestClass(ITestClassFactory factory) {
		this(factory,null);
	}
	
	public AbstractAddConstructorOfTestClass(ITestClassFactory factory, Class<?>[] paramesClasses) {
		super(factory);
		this.paramesClasses = paramesClasses;
	}

	@Override
	public CtClass createTestClass(Class<?> clazz) throws Exception {
		CtClass ctClass = super.createTestClass(clazz);
		CtClass[] parameCtClass = new CtClass[] {};
		// 如果参数不为空，就创建一个带参数的构造函数
		if(this.paramesClasses!=null) {
			ClassPool cp = ClassPool.getDefault();
			parameCtClass = new CtClass[this.paramesClasses.length];
			for(int i=0;i<this.paramesClasses.length;i++) {
				parameCtClass[i]=cp.makeClass(this.paramesClasses[i].getName());
			}
		}
		CtConstructor ctConstructor = new CtConstructor(parameCtClass, ctClass);
		ctConstructor.setModifiers(Modifier.PUBLIC);
		ctConstructor.setBody(this.body());
		ctClass.addConstructor(ctConstructor);
		return ctClass;
	}

	public abstract String body();
}
package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Map;

import com.cat2bug.junit.util.JavassistUtils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;

/**
 * 添加类方法
 * @author yuzhantao
 *
 */
public abstract class AbstractAddMethodOfTestClass extends AbstractTestClassDecorator {
	private Class<?>[] paramesClasses;
	private String name;
	private Map<Class<? extends Annotation>, Map<String, Object>> annoations;
	public AbstractAddMethodOfTestClass(ITestClassFactory factory,String name) {
		this(factory,name,null,null);
	}
	
	public AbstractAddMethodOfTestClass(ITestClassFactory factory, String name,Class<?>[] paramesClasses,Map<Class<? extends Annotation>, Map<String, Object>> annoations) {
		super(factory);
		this.name=name;
		this.paramesClasses = paramesClasses;
		this.annoations=annoations;
	}

	@Override
	public CtClass createTestClass(Class<?> clazz) throws Exception {
		CtClass ctClass = super.createTestClass(clazz);
		ClassFile classFile = ctClass.getClassFile();
		ConstPool constpool = classFile.getConstPool();
		CtClass[] parameCtClass = new CtClass[0];
		// 添加参数
		if(this.paramesClasses!=null) {
			ClassPool cp = ClassPool.getDefault();
			parameCtClass = new CtClass[this.paramesClasses.length];
			for(int i=0;i<this.paramesClasses.length;i++) {
				parameCtClass[i]=cp.makeClass(this.paramesClasses[i].getName());
			}
		}
		CtMethod method = new CtMethod(CtClass.voidType, this.name, parameCtClass, ctClass); //  创建方法对象
		// 添加方法注解
		if (this.annoations != null) {
			AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			for (Map.Entry<Class<? extends Annotation>, Map<String, Object>> a : this.annoations.entrySet()) {
				javassist.bytecode.annotation.Annotation annotation = new javassist.bytecode.annotation.Annotation(
						a.getKey().getName(), constpool);
				// 添加注解参数
				if (a.getValue() != null) {
					for (Map.Entry<String, Object> param : a.getValue().entrySet()) {
						if (param.getValue().getClass().isArray()) {
							int len = Array.getLength(param.getValue());
							MemberValue[] mvs = new MemberValue[len];
							for (int i = 0; i < len; i++) {
								mvs[i] = JavassistUtils.obj2MemberObj(param.getValue(), constpool);
							}
							ArrayMemberValue amv = new ArrayMemberValue(constpool);
							amv.setValue(mvs);
							annotation.addMemberValue(param.getKey(), amv);
						} else {
							annotation.addMemberValue(param.getKey(),
									JavassistUtils.obj2MemberObj(param.getValue(), constpool));
						}
					}
				}

				attr.addAnnotation(annotation);
			}
			method.getMethodInfo().addAttribute(attr);
		}
		
		method.setBody(this.body(ctClass));
		ctClass.addMethod(method);
		return ctClass;
	}

	public abstract String body(CtClass ctClass) throws Exception;
}

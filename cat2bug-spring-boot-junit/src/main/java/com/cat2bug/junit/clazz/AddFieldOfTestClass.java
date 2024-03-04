package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Map;

import com.cat2bug.junit.util.JavassistUtils;

import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;

/**
 * 在测试类中添加日志变量
 * 
 * @author yuzhantao
 *
 */
public class AddFieldOfTestClass extends AbstractTestClassDecorator {
	private Class<?> clazz;
	private String name;
	private Map<Class<? extends Annotation>, Map<String, Object>> annoations;

	public AddFieldOfTestClass(ITestClassFactory factory, Class<?> clazz, String name) {
		this(factory, clazz, name, null);
	}

	public AddFieldOfTestClass(ITestClassFactory factory, Class<?> clazz, String name,
			Map<Class<? extends Annotation>, Map<String, Object>> annotations) {
		super(factory);
		this.clazz = clazz;
		this.name = name;
		this.annoations = annotations;
	}

	@Override
	public CtClass createTestClass(Class<?> clazz) throws Exception {
		CtClass ctClass = super.createTestClass(clazz);
		ClassFile classFile = ctClass.getClassFile();
		ConstPool constpool = classFile.getConstPool();

		// 添加日志变量
		CtField ctField = new CtField(ctClass.getClassPool().get(this.clazz.getName()), this.name, ctClass);
		ctField.setModifiers(Modifier.PUBLIC);

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
			ctField.getFieldInfo().addAttribute(attr);
		}
		ctClass.addField(ctField);
		return ctClass;
	}

}

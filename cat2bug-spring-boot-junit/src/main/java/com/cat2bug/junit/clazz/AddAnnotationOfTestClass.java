package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import com.cat2bug.junit.util.JavassistUtils;

import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;

/**
 * 向类头添加注解
 * @author yuzhantao
 *
 */
public class AddAnnotationOfTestClass extends AbstractTestClassDecorator {

	private Class<? extends Annotation> annotationClass; // 要添加的注解类
	private Map<String, Object> params; // 要添加到注解的参数

	public AddAnnotationOfTestClass(ITestClassFactory factory, Class<? extends Annotation> annotationClass) {
		this(factory,annotationClass,null);
	}
	
	/**
	 * 构造函数
	 * @param factory	测试类工厂
	 * @param annotationClass	要添加的注解类
	 * @param params	注解中的参数（非必填）
	 */
	public AddAnnotationOfTestClass(ITestClassFactory factory, Class<? extends Annotation> annotationClass,
			Map<String, Object> params) {
		super(factory);
		this.annotationClass = annotationClass;
		this.params = params;
	}

	@Override
	public CtClass createTestClass(Class<?> clazz) throws Exception {
		CtClass ctClass = super.createTestClass(clazz);
		ClassFile classFile = ctClass.getClassFile();
		ConstPool constpool = classFile.getConstPool();

		AnnotationsAttribute attr = null;
		List<AttributeInfo> attrs = classFile.getAttributes();
		for (AttributeInfo ai : attrs) {
			if (ai instanceof AnnotationsAttribute) {
				attr = (AnnotationsAttribute) ai;
				break;
			}
		}
		if (attr == null) {
			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		}
		javassist.bytecode.annotation.Annotation annotation = new javassist.bytecode.annotation.Annotation(
				this.annotationClass.getName(), constpool);		// 创建指定类的注解
		// 如果注解参数非空，就添加
		if (this.params != null && this.params.size() > 0) {
			for (Map.Entry<String, Object> param : this.params.entrySet()) {
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
					MemberValue obj = JavassistUtils.obj2MemberObj(param.getValue(), constpool);
					annotation.addMemberValue(param.getKey(), obj);
				}
			}
		}
		try {
			attr.addAnnotation(annotation);
		}catch (Exception e){
			e.printStackTrace();
		}
		classFile.addAttribute(attr);
		return ctClass;
	}

	
}

package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cat2bug.junit.Cat2BugSpringRunner;
import com.cat2bug.junit.util.HttpUtils;
import com.cat2bug.junit.util.ParamMethodUtil;
import com.cat2bug.junit.vo.TestParameter;
import javassist.ClassPool;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;


import javassist.CtClass;

public class SpringControllerTestClassFactory {
	public Class<?> createTestClass(Class<?> testClass, Class<?> clazz) throws Exception {
		String proxyClassName = clazz.getSimpleName() + "Test"; // 测试类的类名
		String packageName = clazz.getPackage().getName(); // 测试类的包名
		String longProxyClassName = packageName + "." + proxyClassName;// 测试类+包名

		ITestClassFactory factory = new TestClassFactory(proxyClassName, packageName);
		// 添加@RunWith注解
		Map<String, Object> runWithParams = new HashMap<>();
		runWithParams.put("value", Cat2BugSpringRunner.class);
		factory = new AddAnnotationOfTestClass(factory, RunWith.class, runWithParams);
		// 将单元测试类上的注解添加到动态创建的测试类上
		Annotation[] anns = testClass.getAnnotations();
		for (Annotation ann : anns) {
			if (ann instanceof RunWith) {
				continue;
			}
			Map<String, Object> annParams = new HashMap<>();
			Method[] annMethods = ann.annotationType().getDeclaredMethods();
			for(Method annMethod : annMethods) {
				Object retAnnValue = annMethod.invoke(ann);
				annParams.put(annMethod.getName(), retAnnValue);
			}
			factory = new AddAnnotationOfTestClass(factory, ann.annotationType(),annParams);
		}
		// 添加@WebAppConfiguration注解
		if (testClass.getAnnotation(WebAppConfiguration.class) == null) {
			factory = new AddAnnotationOfTestClass(factory, WebAppConfiguration.class);
		}
		// 添加@SpringBootTest注解
		if (testClass.getAnnotation(SpringBootTest.class) == null) {
			factory = new AddAnnotationOfTestClass(factory, SpringBootTest.class);
		}
		// 添加日志对象
		factory = new AddFieldOfTestClass(factory, Log.class, "log");
		// 添加WebContext对象
		Map<Class<? extends Annotation>, Map<String, Object>> webContextAnnotationParams = new HashMap<>();
		webContextAnnotationParams.put(Autowired.class, null);
		factory = new AddFieldOfTestClass(factory, WebApplicationContext.class, "webContext",
				webContextAnnotationParams);
		// 添加MockMvc对象
		factory = new AddFieldOfTestClass(factory, MockMvc.class, "mock");
		// 添加ApplicationContext对象
		Map<Class<? extends Annotation>, Map<String, Object>>  applicationContextAnnotationParams = new HashMap<>();
		applicationContextAnnotationParams.put(Autowired.class, null);
		factory = new AddFieldOfTestClass(factory, ApplicationContext.class, "context", applicationContextAnnotationParams);

		// 构造函数
		factory = new AbstractAddConstructorOfTestClass(factory) {
			@Override
			public String body() {
				return "{ this.log=org.apache.commons.logging.LogFactory.getLog(\"" + longProxyClassName + "\"); }";
			}
		};
		// @before函数
		Map<Class<? extends Annotation>, Map<String, Object>> beforeAnnotationParams = new HashMap<>();
		beforeAnnotationParams.put(Autowired.class, null);
		factory = new AbstractAddMethodOfTestClass(factory, "before", null, beforeAnnotationParams) {
			@Override
			public String body(CtClass ctClass) {
				// 在测试启动前初始化MockMvc对象
				return "{mock = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(webContext).build();}";
			}
		};

		Set<Method> methods = scanControllerMethod(clazz);
		// 从测试用例中查找拼配的生成参数的方法，添加到测试类中
		for (Method m : methods) {
			ClassPool pool = ClassPool.getDefault();
			CtClass srcClass = pool.getCtClass(m.getDeclaringClass().getName()); // 获取原始类
			CtMethod srcMethod = srcClass.getDeclaredMethod(m.getName()); // 获取原始类方法
			MethodInfo methodInfo = srcMethod.getMethodInfo();
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			if (codeAttribute != null) {
				LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
						.getAttribute(LocalVariableAttribute.tag);
				int paramLen = srcMethod.getParameterTypes().length; // 参数数量
				Object[][] ans = srcMethod.getParameterAnnotations(); // 获取参数注解
				int pos = Modifier.isStatic(srcMethod.getModifiers()) ? 0 : 1; // 非静态的成员函数的第一个参数是this
				for (int i = 0; i < paramLen; i++) {
					String paramName = attr.variableName(i + pos); // 参数名称
					String paramType = srcMethod.getParameterTypes()[i].getName(); // 参数类型
					String methodName = ParamMethodUtil.createMethodName(m.getName(),paramName,paramType);
					factory= new AddArgeMethodOfTestClass(factory,methodName, clazz, longProxyClassName, m.getName(),paramName, paramType);
				}

			}
		}

		// 添加测试方法
		Map<Class<? extends Annotation>, Map<String, Object>> testMethodAnnotationParams = new HashMap<>();
		testMethodAnnotationParams.put(Test.class, null);
		for (Method m : methods) {
			String testMethodName = "test" + m.getName().substring(0, 1).toUpperCase() + m.getName().substring(1);
			// 根据controller的不同类型注解选择不同的方法工厂创建测试方法
			if (m.getAnnotation(GetMapping.class) != null) {
				factory = new AddHttpGetOfTestMethod(factory, testMethodName, m, null, testMethodAnnotationParams);
			} else if (m.getAnnotation(PostMapping.class) != null) {
				factory = new AddHttpPostOfTestMethod(factory, testMethodName, m, null, testMethodAnnotationParams);
			} else if (m.getAnnotation(PutMapping.class) != null) {
				factory = new AddHttpPutOfTestMethod(factory, testMethodName, m, null, testMethodAnnotationParams);
			} else if (m.getAnnotation(DeleteMapping.class) != null) {
				factory = new AddHttpDeleteOfTestMethod(factory, testMethodName, m, null, testMethodAnnotationParams);
			} else if (m.getAnnotation(PatchMapping.class) != null) {
//				factory = new AddHttpGetOfTestMethod(factory, testMethodName,m, null, testMethodAnnotationParams);
			} else if (m.getAnnotation(RequestMapping.class) != null) {
				RequestMapping rms = m.getAnnotation(RequestMapping.class);
				if (rms.method().length > 0) {
					for (RequestMethod rm : rms.method()) {
						switch (rm) {
						case GET:
							factory = new AddHttpGetOfTestMethod(factory, testMethodName, m, null,
									testMethodAnnotationParams);
							break;
						case HEAD:
							break;
						case POST:
							factory = new AddHttpPostOfTestMethod(factory, testMethodName, m, null,
									testMethodAnnotationParams);
							break;
						case PUT:
							factory = new AddHttpPutOfTestMethod(factory, testMethodName, m, null,
									testMethodAnnotationParams);
							break;
						case PATCH:
							break;
						case DELETE:
							factory = new AddHttpDeleteOfTestMethod(factory, testMethodName, m, null,
									testMethodAnnotationParams);
							break;
						case OPTIONS:
							break;
						case TRACE:
							break;
						}
					}
				} else {
					factory = new AddHttpGetOfTestMethod(factory, testMethodName, m, null, testMethodAnnotationParams);
				}
			}
		}

		CtClass ctClass = factory.createTestClass(clazz);
		this.writeFile(ctClass);
		Class<?> cs = ctClass.toClass();
		return cs;
	}

	private void writeFile(CtClass ctClass) {
		try {
			ctClass.writeFile("./target/cat2bug-junit-classes");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 扫描Controller类中的接口方法
	 * 
	 * @param testClass 需要测试的类
	 * @return 接口方法类集合
	 */
	private Set<Method> scanControllerMethod(Class<?> testClass) {
		Set<Method> ret = new HashSet<>();
		Method[] methods = testClass.getMethods();
		for (Method m : methods) {
			if (m.getAnnotation(GetMapping.class) != null || m.getAnnotation(PostMapping.class) != null
					|| m.getAnnotation(PutMapping.class) != null || m.getAnnotation(DeleteMapping.class) != null
					|| m.getAnnotation(RequestMapping.class) != null || m.getAnnotation(PatchMapping.class) != null) {
				ret.add(m);
			}
		}
		return ret;
	}
}

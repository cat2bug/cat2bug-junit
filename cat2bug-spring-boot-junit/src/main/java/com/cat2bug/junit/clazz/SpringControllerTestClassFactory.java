package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cat2bug.junit.Cat2BugSpringRunner;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;


import javassist.CtClass;

public class SpringControllerTestClassFactory {
	public Class<?> createTestClass(Class<?> testClass, Class<?> clazz) throws Exception {
		String className = clazz.getSimpleName() + "Test"; // 测试类的类名
		String packageName = clazz.getPackage().getName(); // 测试类的包名
		String longClassName = packageName + "." + className;// 测试类+包名

		ITestClassFactory factory = new TestClassFactory(className, packageName);
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
		// 构造函数
		factory = new AbstractAddConstructorOfTestClass(factory) {
			@Override
			public String body() {
				return "{ this.log=org.apache.commons.logging.LogFactory.getLog(\"" + longClassName + "\"); }";
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

		// 添加测试方法
		Set<Method> methods = scanControllerMethod(clazz);
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
		Class<?> cs = ctClass.toClass();
		return cs;
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

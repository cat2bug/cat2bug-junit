package com.cat2bug.junit.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import com.cat2bug.junit.util.ParamMethodUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cat2bug.junit.util.HttpUtils;
import com.cat2bug.junit.vo.ControllerTestMethod;
import com.cat2bug.junit.vo.TestParameter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * 添加测试http接口的方法
 * @author yuzhantao
 *
 */
public abstract class AbstractAddHttpAPIOfTestMethod extends AbstractAddMethodOfTestClass {
	protected Method srcMethod;

	public AbstractAddHttpAPIOfTestMethod(ITestClassFactory factory, String name, Method srcMethod, Class<?>[] paramesClasses,
			Map<Class<? extends Annotation>, Map<String, Object>> annoations) {
		super(factory, name, paramesClasses, annoations);
		this.srcMethod = srcMethod;
	}

	@Override
	public String body(CtClass ctClass) throws Exception {
		String url = HttpUtils.getUrl(this.srcMethod);
		if (url == null)
			return null;

		String methodName = "test" + this.srcMethod.getName().substring(0, 1).toUpperCase()
				+ this.srcMethod.getName().substring(1);
		// 创建测试方法
		CtMethod ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[] {}, ctClass);
		ctMethod.setModifiers(Modifier.PUBLIC);

		// 向测试用例服务中添加方法信息
		ControllerTestMethod testMethod = new ControllerTestMethod(ctClass, ctMethod);
		List<TestParameter> testParameterList = new ArrayList<>();
		testMethod.setParameters(testParameterList);
		TestCaseService.getInstance().addTestMethod(testMethod);

		// 创建方法体
		StringBuffer body = new StringBuffer();
		body.append("\n{");
		body.append("\n log.info(\"====================================================================\");");
		body.append("\n log.info(\"测试方法:" + methodName + "\");");
		body.append("\n " + ControllerTestMethod.class.getName() + " testMethod = ("+ControllerTestMethod.class.getName()+")(" + TestCaseService.class.getName()
				+ ".getInstance().getTestMethod(\"" + testMethod.getLongName() + "\"));");
		body.append("\n java.util.Map params=testMethod.getParameterMap();");
		body.append("\n java.lang.Object requestBodyParam=testMethod.getRequestBodyParameterValue();");
		// 遍历参数
		ClassPool pool = ClassPool.getDefault();
		CtClass srcClass = pool.getCtClass(this.srcMethod.getDeclaringClass().getName()); // 获取原始类
		CtMethod srcMethod = srcClass.getDeclaredMethod(this.srcMethod.getName()); // 获取原始类方法
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

				if (ans[i] != null && ans[i].length > 0) {
					String paramMethodName = ParamMethodUtil.createMethodName(this.srcMethod.getName(),paramName,paramType);
					for (Object pa : ans[i]) {
						if (pa instanceof PathVariable) { // 如果是路径参数
							url = this.pathParamHandler(url, paramName, String.format("this.%s()",paramMethodName));
							break;
						} else if (pa instanceof RequestParam) { // 如果是请求参数
							body.append(String.format("\n params.put(%s,this.%s());",paramName,paramMethodName));
						} else if (pa instanceof RequestBody) { // 如果是Body请求参数
							body.append(String.format("\n requestBodyParam = this.%s();",paramMethodName));
						}
					}
				} else {
					body.append(String.format("\n params.put(%s,this.%s());",paramName,
							ParamMethodUtil.createMethodName(this.srcMethod.getName(),paramName,paramType)));
				}
			}
			String newUrl = Arrays.stream(url.split("\n")).map(s->{
				if(s.matches("^(this\\.)([A-Za-z0-9]*)(\\(\\))$")) {
					return String.format("String.valueOf(%s)",s);
				} else {
					return "\""+s+"\"";
				}
			}).collect(Collectors.joining(" + "));
			body.append("\n " + HttpUtils.class.getName() + "."+this.getTestHttpMethodName()+"(mock," + newUrl + ",params,requestBodyParam);");
		}
		body.append("\n}");
		return body.toString();
	}

	/**
	 * 替换路径中的参数
	 * @param url	源接口路径
	 * @param name	需要替换的字段名
	 * @param value	需要替换的字段值
	 * @return	接口路径
	 */
	private String pathParamHandler(String url, String name, String value) {
		return url.replace(" ", "").replace("{" + name + "}","\n"+value+"\n");
	}
	
	public abstract String getTestHttpMethodName();
}

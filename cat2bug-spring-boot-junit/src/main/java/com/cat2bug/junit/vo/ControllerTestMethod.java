package com.cat2bug.junit.vo;

import javassist.CtClass;
import javassist.CtMethod;

public class ControllerTestMethod extends TestMethod {
	private TestParameter requestBodyParameter;

	public ControllerTestMethod(CtClass ctClass, CtMethod ctMethod) {
		super(ctClass, ctMethod);
	}

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TestParameter getRequestBodyParameter() {
		return requestBodyParameter;
	}

	public Object getRequestBodyParameterValue() {
		if (requestBodyParameter == null) {
			return null;
		} else {
			return requestBodyParameter.getValue();
		}
	}

	public void setRequestBodyParameter(TestParameter requestBodyParameter) {
		this.requestBodyParameter = requestBodyParameter;
	}

}

package com.cat2bug.junit.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class BugCloudPushReportRule implements TestRule {
	private static final Log logger = LogFactory.getLog(BugCloudPushReportRule.class);
	
	@Override
	public Statement apply(Statement base, Description description) {
		Statement newStatement = new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					logger.info("执行前 name:"+description.getMethodName());
					base.evaluate();
					
					if(base instanceof RunBefores) {
						logger.info("执行前=== 继承了RunBefores name:"+description.getMethodName());
					}
					
					if(base instanceof RunAfters) {
						logger.info("执行后=== 继承了RunAfters name:"+description.getMethodName());
					}
				}catch(Exception e) {
					logger.info("===执行错误=== error:"+e.getMessage());
				}
			}
			
		};
		return newStatement;
	}

}

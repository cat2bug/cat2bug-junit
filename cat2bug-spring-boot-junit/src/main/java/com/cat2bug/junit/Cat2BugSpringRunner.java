package com.cat2bug.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cat2bug.junit.listener.BugCloudRunListener;

/**
 * 基于BugCloud的SpringRunner
 * 
 * @author yuzhantao
 *
 */
public class Cat2BugSpringRunner extends SpringJUnit4ClassRunner {
	private Class<?> testClass;

	public Cat2BugSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		this.testClass = clazz;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.addListener(new BugCloudRunListener(this.testClass));
		super.run(notifier);
	}
}

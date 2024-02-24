package com.cat2bug.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.cat2bug.junit.listener.BugCloudRunListener;

/**
 * 基于BugCloud的默认Runner
 * 
 * @author yuzhantao
 *
 */
public class Cat2BugRunner extends BlockJUnit4ClassRunner {
	private Class<?> testClass;

	public Cat2BugRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		this.testClass = clazz;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.addListener(new BugCloudRunListener(this.testClass));
		super.run(notifier);
	}

}

package com.cat2bug.junit.demo;

import com.cat2bug.junit.Cat2BugRunner;
import com.cat2bug.junit.annotation.PushDefect;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-25 22:16
 * @Version: 1.0.0
 */
@RunWith(Cat2BugRunner.class)
@PushDefect
public class Cat2BugRunnerTest {
    @Test
    public void testRuleTrue() {
        Assert.assertTrue(true);
    }

    @Test
    public void testRuleFalse() {
        Assert.assertTrue(false);
    }
}

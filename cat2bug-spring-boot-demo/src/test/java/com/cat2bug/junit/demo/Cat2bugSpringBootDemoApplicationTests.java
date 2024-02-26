package com.cat2bug.junit.demo;

import com.cat2bug.junit.Cat2BugAutoSpringSuite;
import com.cat2bug.junit.annotation.AutoTestScan;
import com.cat2bug.junit.annotation.PushDefect;
import com.cat2bug.junit.annotation.RandomParameter;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@RunWith(Cat2BugAutoSpringSuite.class)
@AutoTestScan(packageName = "com.cat2bug.junit.demo.controller")
@PushDefect(
//        host = "http://127.0.0.1:2020",
//        projectKey = "20240225012438h19zzdb6sd1ahazj",
        handler = "张三")
public class Cat2bugSpringBootDemoApplicationTests {
    /**
     * 计算接口方法中，参数名包含Id的字符型的返回值。
     *
     * @return
     */
    @RandomParameter(parameterName = ".*Id.*")
    public String userId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 计算接口方法中，参数名等于name的的返回值。
     *
     * @return
     */
    @RandomParameter(parameterName = "name")
    public String name() {
        String[] names = { "唐玄奘", "孙悟空", "猪八戒", "沙悟净" };
        return names[(int) (Math.random() * names.length)];
    }
}

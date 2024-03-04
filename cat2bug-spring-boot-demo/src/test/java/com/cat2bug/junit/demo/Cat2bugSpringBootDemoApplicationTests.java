package com.cat2bug.junit.demo;

import com.cat2bug.junit.Cat2BugAutoSpringSuite;
import com.cat2bug.junit.annotation.AutoTestScan;
import com.cat2bug.junit.annotation.PushDefect;
import com.cat2bug.junit.annotation.RandomParameter;
import com.cat2bug.junit.demo.entity.User;
import com.cat2bug.junit.demo.service.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(Cat2BugAutoSpringSuite.class)
@AutoTestScan(packageName = "com.cat2bug.junit.demo.controller")
@PushDefect
//@Transactional
public class Cat2bugSpringBootDemoApplicationTests {
    /**
     * 插入用户
     * @param userRepository
     * @return
     */
    @RandomParameter(className = "com.cat2bug.junit.demo.controller.UserController", methodName = "updateUser", parameterName = "user")
    public User insertUser(
            @Autowired UserRepository userRepository
    ) {
        User user = new User(1L,"admin","admin");
        return userRepository.save(user);
    }

    /**
     * 计算接口方法中，参数名包含Id的字符型的返回值。
     *
     * @return 用户ID
     */
    @RandomParameter(parameterName = ".*Id.*")
    public long userId() {
        return 1L;
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

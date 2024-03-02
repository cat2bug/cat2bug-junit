package com.cat2bug.junit.annotation;

import java.lang.annotation.*;

/**
 * @Author: yuzhantao
 * @CreateTime: 2024-02-27 00:59
 * @Version: 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerTest {
    /**
     * 是否启动自动化接口测试
     * @return
     */
    boolean enable() default true;

    Class<? extends Throwable> getMethodAssertThrowable() default Throwable.class;

    Class<? extends Throwable> postMethodAssertThrowable() default Throwable.class;


    Class<? extends Throwable> putMethodAssertThrowable() default Throwable.class;


    Class<? extends Throwable> deleteMethodAssertThrowable() default Throwable.class;
}

package com.cat2bug.junit.annotation;

import java.lang.annotation.*;

/**
 * 用户配置接口测试信息
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerTest {
    /**
     * 是否启动自动化接口测试
     * @return  true为自动测试
     */
    boolean enable() default true;

    Class<? extends Throwable> getMethodAssertThrowable() default Throwable.class;

    Class<? extends Throwable> postMethodAssertThrowable() default Throwable.class;


    Class<? extends Throwable> putMethodAssertThrowable() default Throwable.class;


    Class<? extends Throwable> deleteMethodAssertThrowable() default Throwable.class;
}

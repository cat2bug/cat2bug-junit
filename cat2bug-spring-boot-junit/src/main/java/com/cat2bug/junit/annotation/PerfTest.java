package com.cat2bug.junit.annotation;

import java.lang.annotation.*;

/**
 * 性能测试
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerfTest {
    /**
     * 是否启动性能测试
     * @return
     */
    boolean enable() default true;
    /**
     * 并发线程数量
     * @return
     */
    int threads() default 1;

    /**
     * 执行次数
     * @return
     */
    int invocations() default 1;

    /**
     * 重复地执行测试至少执行多少秒，0表示不参考此参数
     * @return
     */
    long duration() default 0;
}

package com.cat2bug.junit.util;

/**
 * 用户给参数创建一个方法的工具类
 */
public class ParamMethodUtil {
    public static String createMethodName(String methodName,String paramName, String paramType) {
        return "get"+methodName+paramName+paramType.replace(".","");
    }
}

package com.cat2bug.junit.clazz;

import com.alibaba.fastjson.JSON;
import com.cat2bug.junit.util.ParamMethodUtil;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 将每个方法的参数已方法的形式体现
 */
public class AddArgeMethodOfTestClass extends AbstractAddMethodOfTestClass {

    private Class<?> srcClass;
    private String proxyClassName;
    private String srcMethodName;
    private String parameterName;
    private String paramType;
    public AddArgeMethodOfTestClass(ITestClassFactory factory, String name, Class<?> srcClass, String proxyClassName, String srcMethodName, String parameterName, String paramType) throws Exception {
        super(factory, name,null,null, toCtClass(paramType));
        this.srcClass=srcClass;
        this.proxyClassName=proxyClassName;
        this.srcMethodName=srcMethodName;
        this.parameterName=parameterName;
        this.paramType=paramType;
    }

    @Override
    public String body(CtClass ctClass) throws Exception {
        StringBuffer sb =new StringBuffer();
        sb.append("{");
        Method testCaseMethod = ParameterService.getInstance().getTestCaseMethod(this.srcClass.getName(), this.proxyClassName, this.srcMethodName,
							this.parameterName, this.paramType);
        // 如果测试用例类中没有生成参数的方法，就写随机创建参数值的方法；如果测试用例中有，就拷贝那个函数，并指定一个新的无参函数调用他，在调接口时，调用这个无参函数
        if(testCaseMethod==null) {
            Object value = ParameterService.getInstance().createParameterRandomValue(this.paramType);
                switch (this.paramType) {
                    case "java.lang.String":
                        sb.append(String.format("return \"%s\";", String.valueOf(value).replace("\"","\\\"")));
                        break;
                    case "int":
                        sb.append(String.format("return Integer.valueOf(%d).intValue();", (Integer)value));
                        break;
                    case "java.lang.Integer":
                        sb.append(String.format("return Integer.valueOf(%d);", (Integer)value));
                        break;
                    case "long":
                        sb.append(String.format("return Long.valueOf(%dL).longValue();", (Long)value));
                        break;
                    case "java.lang.Long":
                        sb.append(String.format("return Long.valueOf(%dL);", (Long)value));
                        break;
                    case "short":
                        sb.append(String.format("return Short.valueOf(%d).shortValue();", (Short)value));
                        break;
                    case "java.lang.Short":
                        sb.append(String.format("return Short.valueOf(%d);", (Short)value));
                        break;
                    case "double":
                        sb.append(String.format("return Double.valueOf(%a).doubleValue();", (Double)value));
                        break;
                    case "java.lang.Double":
                        sb.append(String.format("return Double.valueOf(%a);", (Double)value));
                        break;
                    case "float":
                        sb.append(String.format("return Float.valueOf(%f).floatValue();", (Float)value));
                        break;
                    case "java.lang.Float":
                        sb.append(String.format("return Float.valueOf(%f);", (Float)value));
                        break;
                    case "bool":
                        sb.append(String.format("return Boolean.valueOf(%b).booleanValue();", (Boolean)value));
                        break;
                    case "java.lang.Boolean":
                        sb.append(String.format("return Boolean.valueOf(%b);", (Boolean)value));
                        break;
                    case "char":
                        sb.append(String.format("return Character.valueOf(%c).charValue();", (Character)value));
                        break;
                    case "java.lang.Character":
                        sb.append(String.format("return Character.valueOf(%c);", (Character)value));
                        break;
                    case "java.util.Date":
                        sb.append(String.format("return %tx;", ((Date)value).getTime()));
                        break;
                    default:
                        String json = JSON.toJSONString(value);
                        sb.append(String.format("return (%s)com.alibaba.fastjson.JSON.parseObject(\"%s\", %s.class);",this.paramType, json.replace("\"","\\\""), this.paramType));
                        break;
                }
        } else { // 拷贝函数并创建无参函数的执行代码
            ClassPool pool = ClassPool.getDefault();
            CtClass srcClass = pool.getCtClass(testCaseMethod.getDeclaringClass().getName()); // 获取原始类
            CtMethod srcMethod = srcClass.getDeclaredMethod(testCaseMethod.getName()); // 获取原始类方法
            MethodInfo methodInfo = srcMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            if (codeAttribute != null) {
                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                        .getAttribute(LocalVariableAttribute.tag);
                int paramLen = srcMethod.getParameterTypes().length; // 参数数量
                Object[][] ans = srcMethod.getParameterAnnotations(); // 获取参数注解
                int pos = Modifier.isStatic(srcMethod.getModifiers()) ? 0 : 1; // 非静态的成员函数的第一个参数是this
                List<String> paramList = new ArrayList<>();
                // 遍历方法中的参数，并通过bean方法获取到
                for (int i = 0; i < paramLen; i++) {
                    String paramName = attr.variableName(i + pos); // 参数名称
                    String paramType = srcMethod.getParameterTypes()[i].getName(); // 参数类型
                    paramList.add(paramName);
                    sb.append(String.format("%s %s = context.getBean(%s.class);",paramType,paramName,paramType));
                }
                String methodName = "src"+name;
                // 将测试用例中创建参数值的方法添加到测试类中
                CtMethod newMethod = CtNewMethod.copy(srcMethod,methodName,ctClass,null);
                Class<?> retCls = srcMethod.getReturnType().getClass();
                ctClass.addMethod(newMethod);
                // 执行并返回刚刚加入到测试类中的创建参数值的方法
                switch (testCaseMethod.getReturnType().getName()) {
                    case "long":
                        sb.append(String.format("return Long.valueOf(this.%s(%s));",methodName,paramList.stream().collect(Collectors.joining(","))));
                        break;
                    case "int":
                        sb.append(String.format("return Integer.valueOf(this.%s(%s));",methodName,paramList.stream().collect(Collectors.joining(","))));
                        break;
                    case "short":
                        sb.append(String.format("return Short.valueOf(this.%s(%s));",methodName,paramList.stream().collect(Collectors.joining(","))));
                        break;
                    case "double":
                        sb.append(String.format("return Double.valueOf(this.%s(%s));",methodName,paramList.stream().collect(Collectors.joining(","))));
                        break;
                    case "Float":
                        sb.append(String.format("return float.valueOf(this.%s(%s));",methodName,paramList.stream().collect(Collectors.joining(","))));
                        break;
                    case "chat":
                        sb.append(String.format("return Character.valueOf(this.%s(%s));",methodName,paramList.stream().collect(Collectors.joining(","))));
                        break;
                    case "bool":
                        sb.append(String.format("return Boolean.valueOf(this.%s(%s));",methodName,paramList.stream().collect(Collectors.joining(","))));
                        break;
                    default:
                        sb.append(String.format("return (%s)this.%s(%s);",this.paramType, methodName,paramList.stream().collect(Collectors.joining(","))));
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 转换Class到CtClass类
     * @param clazzName 类型
     * @return  CtClass类
     * @throws Exception 异常
     */
    private static CtClass toCtClass(String clazzName) throws Exception {
        ClassPool cp = ClassPool.getDefault();
        switch (clazzName) {
            case "int":
                return CtClass.intType;
            case "long":
                return CtClass.longType;
            case "short":
                return CtClass.shortType;
            case "double":
                return CtClass.doubleType;
            case "float":
                return CtClass.floatType;
            case "bool":
                return CtClass.booleanType;
            case "char":
                return CtClass.charType;
            default:
                return cp.getCtClass(clazzName);
        }
    }
}

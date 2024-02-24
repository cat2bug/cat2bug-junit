package com.cat2bug.junit.clazz;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.cat2bug.junit.annotation.RandomParameter;

/**
 * 参数服务类
 * 
 * @author yuzhantao
 *
 */
public class ParameterService {
	private Map<String, Object> createParameterClassMap = new ConcurrentHashMap<>();

	private static ParameterService instance;

	public static ParameterService getInstance() {
		if (instance == null) {
			instance = new ParameterService();
		}
		return instance;
	}

	/**
	 * 添加参数创建类
	 * 
	 * @param createClass 		可以创建参数值的类
	 * @param useClassNames    	使用创建类的其它类数组
	 * @throws Exception		
	 */
	public void addParameterCreateClass(Class<?> createClass, String[] useClassNames) throws Exception {
		Object createObj = createClass.newInstance();
		for (String cls : useClassNames) {
			this.createParameterClassMap.put(cls, createObj);
		}
	}

	/**
	 * 获取参数值
	 * 
	 * @param useClassName  使用创建类的测试类
	 * @param methodName    测试方法
	 * @param parameterName 参数名
	 * @param paramType 	参数类型
	 * @return				返回创建的参数值
	 * @throws Exception
	 */
	public Object createParameterValue(String useClassName, String methodName, String parameterName, String paramType)
			throws Exception {
		if (this.createParameterClassMap.containsKey(useClassName)) {
			Object createInstance = this.createParameterClassMap.get(useClassName); // 获取创建类的实例
			// 遍历用户写的测试类中的方法，查找是否有定义随机参数的函数，如果有，就用用户函数计算参数值
			Method[] methods = createInstance.getClass().getMethods();
			for (Method m : methods) {
				RandomParameter rp = m.getAnnotation(RandomParameter.class);
				if (rp == null)
					continue;
				boolean isMatch = Pattern.matches(rp.className(), useClassName);
				if (!isMatch && "".equals(rp.className()) == false)
					continue;
				isMatch = Pattern.matches(rp.methodName(), methodName);
				if (!isMatch && "".equals(rp.methodName()) == false)
					continue;
				isMatch = Pattern.matches(rp.parameterName(), parameterName);
				if (!isMatch && "".equals(rp.parameterName()) == false)
					continue;
				if (paramType.equals(m.getReturnType().getName()) == false) {
					continue;
				}
				return (String) m.invoke(createInstance, new Object[] {});
			}
			return createParameterValueByAI(parameterName, paramType); // 如果没有自定义函数，就用内部算法计算参数值
		} else {
			return createParameterValueByAI(parameterName, paramType); // 如果没有自定义函数，就用内部算法计算参数值
		}
	}

	/**
	 * 根据智能化算法创建参数值
	 * 
	 * @param parameterName
	 * @param paramType
	 * @return
	 */
	private static Object createParameterValueByAI(String parameterName, String paramType) {
		switch (paramType) {
		case "java.lang.String":
			return createStringValue();
		case "int":
		case "java.lang.Integer":
			return createIntegerValue();
		case "long":
		case "java.lang.Long":
			return createLongValue();
		case "short":
		case "java.lang.Short":
			return createShortValue();
		case "double":
		case "java.lang.Double":
			return createDoubleValue();
		case "float":
		case "java.lang.Float":
			return createFloatValue();
		case "bool":
		case "java.lang.Boolean":
			return createBooleanValue();
		case "char":
		case "java.lang.Character":
			return createCharacterValue();
		case "java.util.Date":
			return createDateValue();
		default:
			return createObjectValue(paramType);
		}
	}

	private static Object createObjectValue(String paramType) {
		try {
			Object obj = Class.forName(paramType).newInstance();
			setClassFieldValue(obj);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

	class a {
		private String a;
		private int b;
		private Date c;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public int getB() {
			return b;
		}

		public void setB(int b) {
			this.b = b;
		}

		public Date getC() {
			return c;
		}

		public void setC(Date c) {
			this.c = c;
		}

	}

	public static void main(String[] args) {
		ParameterService d = new ParameterService();
		Object a = d.new a();
		setClassFieldValue(a);
		System.out.println(JSON.toJSONString(a));
	}

	private static void setClassFieldValue(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field f : fields) {
			// 如果是静态或常量，就跳出
			if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers()))
				continue;
			Type t = f.getGenericType();
			String typeName = t.getTypeName();
			Object value = createParameterValueByAI(f.getName(), typeName);
			try {
				f.setAccessible(true);
				f.set(obj, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("type:" + typeName + "   " + f.getName() + "=" + value);
		}
	}

	private static String createStringValue() {
		return UUID.randomUUID().toString();
	}

	private static int createIntegerValue() {
		return (int) (Math.random() * Integer.MAX_VALUE);
	}

	private static short createShortValue() {
		return (short) (Math.random() * Short.MAX_VALUE);
	}

	private static double createDoubleValue() {
		return Math.random() * Double.MAX_VALUE;
	}

	private static float createFloatValue() {
		return (float) (Math.random() * Float.MAX_VALUE);
	}

	private static boolean createBooleanValue() {
		return Math.random() > 0.5;
	}

	private static char createCharacterValue() {
		return (char) (Math.random() * Character.MAX_VALUE);
	}

	private static long createLongValue() {
		return (long) (Math.random() * Long.MAX_VALUE);
	}

	private static Date createDateValue() {
		return new Date((long) (Math.random() * Long.MAX_VALUE));
	}
}

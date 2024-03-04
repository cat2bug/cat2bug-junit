package com.cat2bug.junit.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;

/**
 * Url资源拼接工具
 * 
 * @author yuzhantao
 *
 */
public class HttpUtils {
	public static String getUrl(Method method) {
		List<String> urls = new ArrayList<String>();
		String headUrl = null;
		String[] getMappingValues = null;
		Annotation classAnnotation = method.getDeclaringClass().getAnnotation(RequestMapping.class);
		if (classAnnotation != null) {
			headUrl = ((RequestMapping) classAnnotation).value()[0];
		} else {
			return null;
		}
		Annotation annotation = method.getAnnotation(GetMapping.class);
		if (annotation != null) {
			getMappingValues = ((GetMapping) annotation).value();
		}
		if (annotation == null) {
			annotation = method.getAnnotation(PostMapping.class);
			if (annotation != null) {
				getMappingValues = ((PostMapping) annotation).value();
			}
		}
		if (annotation == null) {
			annotation = method.getAnnotation(PutMapping.class);
			if (annotation != null) {
				getMappingValues = ((PutMapping) annotation).value();
			}
		}
		if (annotation == null) {
			annotation = method.getAnnotation(DeleteMapping.class);
			if (annotation != null) {
				getMappingValues = ((DeleteMapping) annotation).value();
			}
		}
		if (annotation == null) {
			annotation = method.getAnnotation(RequestMapping.class);
			if (annotation != null) {
				RequestMapping rm = (RequestMapping) annotation;
				boolean isHavsGetMapping = false;
				for (RequestMethod r : rm.method()) {
					if (r == RequestMethod.GET) {
						isHavsGetMapping = true;
					}
				}
				if (isHavsGetMapping) {
					getMappingValues = ((RequestMapping) annotation).value();
				}
			}
		}
		if (annotation != null) {
			if (getMappingValues != null && getMappingValues.length > 0) {
				for (String shortUrl : getMappingValues) {
					urls.add(headUrl);
					urls.add(shortUrl);
					return HttpUtils.getUrl(urls);
				}
			} else {
				urls.add(headUrl);
				return HttpUtils.getUrl(urls);
			}
		}
		return null;
	}

	public static String createPropertyValue(String parameterType) {
//		if (argType == Signature.class) {
//	        return signature;
//	    } else if (argType == Request.class) {
//	        return signature.request();
//	    } else if (argType == Response.class) {
//	        return signature.response();
//	    } else if (argType == Session.class || argType == HttpSession.class) {
//	        return signature.request().session();
//	    } else if (argType == FileItem.class) {
//	        return new ArrayList<>(signature.request().fileItems().values()).get(0);
//	    } else if (argType == ModelAndView.class) {
//	        return new ModelAndView();
//	    } else if (argType == Map.class) {
//	        return signature.request().parameters();
//	    } else if (argType == Optional.class) {
//	        ParameterizedType firstParam = (ParameterizedType) parameter.getParameterizedType();
//	        Type paramsOfFirstGeneric = firstParam.getActualTypeArguments()[0];
//	        Class<?> modelType = ReflectKit.form(paramsOfFirstGeneric.getTypeName());
//	        return Optional.ofNullable(parseModel(modelType, signature.request(), null));
//	    } else {
//	        return parseModel(argType, signature.request(), null);
		if("java.lang.String".equals(parameterType)){
			return createStringValue();
		} else if("java.lang.Integer".equals(parameterType)) {
			return createIntegerValue();
		}
		return null;
	}

	private static String createStringValue() {
		return UUID.randomUUID().toString();
	}

	private static String createIntegerValue() {
		return String.valueOf(Math.random() * Integer.MAX_VALUE);
	}

	public static void testGet(MockMvc mock, String url, Map<?, ?> params, Object body) throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(url) // 请求的url,请求的方法是get
				.contentType(MediaType.APPLICATION_JSON); // 数据的格式
		if (body != null) {
			builder = builder.content(JSON.toJSONString(body)); // 数据的格式
		}
		if (params != null) {
			for (Map.Entry<?, ?> item : params.entrySet()) {
				if (item.getValue() == null)
					continue;
				builder = builder.param((String) item.getKey(), (String) item.getValue()); // 添加参数
			}
		}

		mock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()) // 返回的状态是200
				.andDo(MockMvcResultHandlers.print()) // 打印出请求和相应的内容
				.andReturn().getResponse().getContentAsString(); // 将相应的数据转换为字符串
	}

	public static void testPost(MockMvc mock, String url, Map<?, ?> params, Object body) throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(url) // 请求的url,请求的方法是post
				.contentType(MediaType.APPLICATION_JSON);
		if (body != null) {
			builder = builder.content(JSON.toJSONString(body)); // 数据的格式
		}
		if (params != null) {
			for (Map.Entry<?, ?> item : params.entrySet()) {
				if (item.getValue() == null)
					continue;
				builder = builder.param((String) item.getKey(), (String) item.getValue()); // 添加参数
			}
		}
		mock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()) // 返回的状态是200
				.andDo(MockMvcResultHandlers.print()) // 打印出请求和相应的内容
				.andReturn().getResponse().getContentAsString(); // 将相应的数据转换为字符串

	}
	
	public static void testPut(MockMvc mock, String url, Map<?, ?> params, Object body) throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url) // 请求的url,请求的方法是post
				.contentType(MediaType.APPLICATION_JSON);
		if (body != null) {
			builder = builder.content(JSON.toJSONString(body)); // 数据的格式
		}
		if (params != null) {
			for (Map.Entry<?, ?> item : params.entrySet()) {
				if (item.getValue() == null)
					continue;
				builder = builder.param((String) item.getKey(), (String) item.getValue()); // 添加参数
			}
		}
		mock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()) // 返回的状态是200
				.andDo(MockMvcResultHandlers.print()) // 打印出请求和相应的内容
				.andReturn().getResponse().getContentAsString(); // 将相应的数据转换为字符串

	}
	
	public static void testDelete(MockMvc mock, String url, Map<?, ?> params, Object body) throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(url) // 请求的url,请求的方法是post
				.contentType(MediaType.APPLICATION_JSON);
		if (body != null) {
			builder = builder.content(JSON.toJSONString(body)); // 数据的格式
		}
		if (params != null) {
			for (Map.Entry<?, ?> item : params.entrySet()) {
				if (item.getValue() == null)
					continue;
				builder = builder.param((String) item.getKey(), (String) item.getValue()); // 添加参数
			}
		}
		mock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()) // 返回的状态是200
				.andDo(MockMvcResultHandlers.print()) // 打印出请求和相应的内容
				.andReturn().getResponse().getContentAsString(); // 将相应的数据转换为字符串

	}

	/**
	 * 获取网址
	 * 
	 * @param cs 网址集合
	 * @return	网址
	 */
	public static String getUrl(List<String> cs) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cs.size(); i++) {
			String s = cs.get(i);
			if (StringUtils.hasLength(s) == false) {
				continue;
			}
			int len = s.indexOf("/");
			if (len != 0) {
				s = "/" + s;
			}
			if (i < cs.size() - 1) {
				len = s.lastIndexOf("/");
				if (len == s.length() - 1) {
					s = s.substring(0, s.length() - 1);
				}
			}
			sb.append(s);
		}
		return sb.toString();
	}
}

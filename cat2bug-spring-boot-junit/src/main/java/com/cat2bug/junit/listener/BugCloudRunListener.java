package com.cat2bug.junit.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cat2bug.junit.annotation.PushDefect;
import com.cat2bug.junit.util.ConfigUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.util.Strings;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.alibaba.fastjson.JSONObject;
import com.cat2bug.junit.vo.RequestApiQuestionVo;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;

public class BugCloudRunListener extends RunListener {
	private static final String CONFIG_FILE = "application.yaml"; // YAML 配置文件路径

	private static final Log logger = LogFactory.getLog(BugCloudRunListener.class);
	/**
	 * 默认API服务地址
	 */
	private final static String DEFAULT_API_HOST = "https://www.cat2bug.com/cloud/";
	/**
	 * 接口提交TOKEN标识
	 */
	private static final String AUTH_TOKEN_HEADER_NAME = "CAT2BUG-API-KEY";
	/**
	 * 推送缺陷api地址
	 */
	private static final String PUSH_DEFECT_URL = "/api/defect";
	/**
	 * 接口内容类型
	 */
	private static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");

	private Class<?> testClass = null;

	public BugCloudRunListener(Class<?> clazz) {
		this.testClass = clazz;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void testRunFinished(Result result) throws Exception {
		PushDefect pushDefect = this.testClass.getAnnotation(PushDefect.class);

		if (pushDefect == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\nTest Class:" + this.testClass.getName() + " 开始测试=========================================");
		sb.append("\n run time:" + result.getRunTime());
		sb.append("\n run count:" + result.getRunCount());
		sb.append("\n ignore count:" + result.getIgnoreCount());
		sb.append("\n failure count:" + result.getFailureCount());
		sb.append("\n---------------------------------------------------");
		sb.append("\n projectKey:" + pushDefect.projectKey());
		sb.append("\n handler:" + pushDefect.handler());
		sb.append("\n---------------------------------------------------");
		for (Failure failure : result.getFailures()) {
			sb.append("\n 测试失败结果： " + failure.toString());
		}

		// 如果不推送，就直接返回。
		if (pushDefect.isPush() == false) {
			sb.append(" Test Class:" + this.testClass + " 测试完成=========================================");
			return;
		}

		String url = this.getPushDefectUrl(pushDefect);
		Preconditions.checkNotNull(url,"host不能为空");
		String projectKey = this.getProjectKey(pushDefect);
		Preconditions.checkNotNull(projectKey,"projectKey不能为空");

		OkHttpClient client = new OkHttpClient();
		JSONObject json = new JSONObject();
		json.put("handleByList", Arrays.asList(this.getHandler(pushDefect)));
		String defectName = result.getFailures().stream().map(
				fail->fail.getDescription().getClassName() + "." + fail.getDescription().getMethodName()
		).collect(Collectors.joining("\n"));
		String defectDescribe = result.getFailures().stream().map(fail->fail.getMessage()).collect(Collectors.joining("\n"));
		json.put("defectName", defectName);
		json.put("defectDescribe", defectDescribe);
		RequestBody formBody = RequestBody.create(FORM_CONTENT_TYPE, String.valueOf(json));
		Request request = new Request.Builder()
				.url(url)
				.header("content-type", "application/json")
				.header(AUTH_TOKEN_HEADER_NAME,projectKey)
				.post(formBody).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.code() == 200) {
				sb.append("\n 提交问题接口完成,返回body:" + response.body().string());
			} else {
				sb.append("\n 提交问题接口失败\n state code:" + response.code() + "\n body:" + response.body().string());
			}
		} catch (Exception e) {
			sb.append("\n 提交问题接口失败\n local error:" + e.getMessage());
		} finally {
			sb.append("\n Test Class:" + this.testClass.getName() + " 测试完成=========================================");
			logger.info(sb.toString());
		}

		super.testRunFinished(result);
	}


	/**
	 * 获取处理人
	 * @param pushDefect
	 * @return
	 */
	private String getHandler(PushDefect pushDefect) {
		if(Strings.isNotBlank(pushDefect.handler())){
			return pushDefect.handler();
		}
		String handler = ConfigUtil.getConfig("cat2bug.push-defect.handler",String.class);
		if(Strings.isNotBlank(handler)) {
			return handler;
		}
		return null;
	}

	/**
	 * 获取项目密钥
	 * @param pushDefect
	 * @return
	 */
	private String getProjectKey(PushDefect pushDefect) {
		if(Strings.isNotBlank(pushDefect.projectKey())){
			return pushDefect.projectKey();
		}
		String projectKey = ConfigUtil.getConfig("cat2bug.push-defect.project-key",String.class);
		if(Strings.isNotBlank(projectKey)) {
			return projectKey;
		}
		return null;

	}

	/**
	 * 获取缺陷推送地址
	 * @param pushDefect
	 * @return
	 */
	private String getPushDefectUrl(PushDefect pushDefect) {
		if(Strings.isNotBlank(pushDefect.host())){
			return pushDefect.host().replaceAll("/$","")+PUSH_DEFECT_URL;
		}
		String host = ConfigUtil.getConfig("cat2bug.push-defect.host",String.class);
		if(Strings.isNotBlank(host)) {
			return host.replaceAll("/$","")+PUSH_DEFECT_URL;
		}
		return null;
	}
}

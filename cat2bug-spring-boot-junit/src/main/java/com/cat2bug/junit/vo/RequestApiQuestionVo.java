package com.cat2bug.junit.vo;

/**
 * 请求的问题API对象
 * @author yuzhantao
 *
 */
public class RequestApiQuestionVo {
	private String title;
	private String content;
	private boolean isSuccess;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}

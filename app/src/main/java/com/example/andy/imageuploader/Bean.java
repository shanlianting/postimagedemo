package com.example.andy.imageuploader;

import java.util.List;


public class Bean {
	private int code;
	private String message;
	private List<String> images;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Bean(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	public List<String> getList() {
		return images;
	}
	public void setList(List<String> list) {
		this.images = list;
	}
	
}

package com.springai.ollama.dto;


public class Details {
	
	private String title;
	private String content;
	private String createdYear;
	public Details(String title, String content, String createdYear) {
		super();
		this.title = title;
		this.content = content;
		this.createdYear = createdYear;
	}
	public Details() {
		super();
		// TODO Auto-generated constructor stub
	}
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
	public String getCreatedYear() {
		return createdYear;
	}
	public void setCreatedYear(String createdYear) {
		this.createdYear = createdYear;
	}
	
	
	

}

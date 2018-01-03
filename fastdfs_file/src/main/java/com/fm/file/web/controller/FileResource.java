package com.fm.file.web.controller;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 资源
 * @author syf
 *
 */
public class FileResource{
	
	private String title;
	
	/**
	 * 来源
	 */
	private String  client;
	
	
	/**
	 * 内容ID
	 */
	private String  contendId;
	
	/**
	 * 内容类型
	 */
	private String  contendType;
	
	
	/**
	 * 类型(1:图片，2：网页，3视频,4音频,5文本 6 压缩文件)
	 */
	private Integer  type=1;
	
	/**
	 * 后缀
	 */
	private String suffix="";
	


	/**
	 * 数据库存放地址
	 */
	private String  url;
	
	/**
	 * 网络地址
	 */
	private String  path;
	
	private String  token;
	
	//大小kb
	private Integer size;
	
	
	/**
	 * 创建时间
	 */
	@JsonIgnore
	private Date createTime=new Date();
	
	/**
	 *  更新 时间
	 */
	@JsonIgnore
	private Date updateTime=new Date();

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getContendId() {
		return contendId;
	}

	public void setContendId(String contendId) {
		this.contendId = contendId;
	}

	public String getContendType() {
		return contendType;
	}

	public void setContendType(String contendType) {
		this.contendType = contendType;
	}



	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	
	
}

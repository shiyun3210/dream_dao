package com.zk.chat.enums;

import org.springframework.util.StringUtils;

/**
 * 响应消息
 * @author syf
 *
 */
public enum CodeMessage {
	
	success("0","ok"), 
	error("-1", "system error"),
	token_invalid("1","token invalid");
	
	private String code;
	private String msg;
	
	CodeMessage(String code,String msg){
		this.code = code;
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	
	public static CodeMessage getInstance(String code){
		if(StringUtils.isEmpty(code)){
			return null;
		}
		for(CodeMessage message : CodeMessage.values()){
			if(message.getCode().equals(code)){
				return message;
			}
		}
		return null;
	}
}

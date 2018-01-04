package com.zk.chat.websocket.manager;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.socket.WebSocketSession;

/**
 * 单聊 会话管理
 * @author syf
 */
public class SingleChatManager {
	
	private static SingleChatManager sessionMananger = new SingleChatManager();
	
	private ConcurrentHashMap<String, WebSocketSession> map = new ConcurrentHashMap<String, WebSocketSession>();
	
	private SingleChatManager(){
		
	}
	
	public static SingleChatManager getInstance() {
		return sessionMananger;
	}
	
	public ConcurrentHashMap<String,WebSocketSession> getMap(){
		return map;
	}
	public Set<String> getSetKey(){
		return map.keySet();
	}
	
	public void addOnlineUser(String uid,WebSocketSession webSocketSession){
		if(StringUtils.isNotBlank(uid)&&webSocketSession!=null){
			WebSocketSession old= map.get(uid);
			if(old==null){
				map.putIfAbsent(uid, webSocketSession);
			}else{
				map.replace(uid, old,webSocketSession);
			}
		}
	}
	
	public void removeOnlineUser(String uid){
		if(StringUtils.isNotBlank(uid)){
			map.remove(uid);
		}
	}
	
	public WebSocketSession getOnlineUser(String uid){
		return StringUtils.isBlank(uid)?null:map.get(uid);
	}
	
	public Collection<WebSocketSession> getAllOnlineUser(){
		return map.values();
	}
	
	public int countOnlineUser(){
		return map.size();
	}
}

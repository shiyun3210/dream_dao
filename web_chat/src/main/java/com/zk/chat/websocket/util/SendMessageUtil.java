package com.zk.chat.websocket.util;

import java.io.IOException;
import java.util.Collection;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.zk.chat.asyn.AsynProcessor;
import com.zk.chat.asyn.SendMsgProcessor;
import com.zk.chat.websocket.manager.SingleChatManager;

public class SendMessageUtil {
	
	/**
	 * 给所有在线用户发送消息
	 * @param message
	 * @throws IOException
	 */
	public static void sendAllUser(TextMessage message){
		System.out.println("发送广播消息："+message.getPayload());
		Collection<WebSocketSession> allUser = SingleChatManager.getInstance().getAllOnlineUser();
		for(WebSocketSession session : allUser){
			AsynProcessor.asynProcess(new SendMsgProcessor(session,message));
		}
	}
	
	/**
	 * 给某个用户发送消息
	 * @param uid
	 * @param touid
	 * @param message
	 */
	public static void sendMessageToUser(String uid,String touid, TextMessage message){
		WebSocketSession session = SingleChatManager.getInstance().getOnlineUser(touid);
		if (session != null && session.isOpen()) {
			System.out.println("发送给用户："+touid+"消息："+message.getPayload());
			AsynProcessor.asynProcess(new SendMsgProcessor(session,message));
		}
	}
	
}

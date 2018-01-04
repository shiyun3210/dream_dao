package com.zk.chat.asyn;

import java.io.IOException;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

public class SendMsgProcessor implements Runnable {

	private WebSocketSession webSocketSession;
	private TextMessage message;
	
	public SendMsgProcessor() {
		
	}
	
	public SendMsgProcessor(WebSocketSession webSocketSession,TextMessage message) {
		this.webSocketSession = webSocketSession;
		this.message = message;
	}

	public WebSocketSession getWebSocketSession() {
		return webSocketSession;
	}

	public void setWebSocketSession(WebSocketSession webSocketSession) {
		this.webSocketSession = webSocketSession;
	}

	public TextMessage getMessage() {
		return message;
	}

	public void setMessage(TextMessage message) {
		this.message = message;
	}

	@Override
	public void run() {
		try {
			webSocketSession.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package com.zk.chat.websocket.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.zk.chat.websocket.interceptor.WebsocketInterceptor;

/**
 * webSocket 配置
 * @author syf
 */
@Component
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	@Autowired
	private MessageHandle handler;

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(handler, "/ws").addInterceptors(new WebsocketInterceptor());

//		registry.addHandler(handler, "/ws/sockjs").addInterceptors(new WebsocketInterceptor()).withSockJS();
	}

}

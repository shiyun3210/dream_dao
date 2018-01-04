package com.zk.chat.websocket.handle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.zk.chat.redis.RedisUserService;
import com.zk.chat.util.DateTools;
import com.zk.chat.util.JacksonUtils;
import com.zk.chat.websocket.manager.SingleChatManager;
import com.zk.chat.websocket.util.SendMessageUtil;

/**
 * 处理器
 * @author syf
 */
@Component("messageHandle")
public class MessageHandle implements WebSocketHandler {
	
	@Autowired
	private RedisUserService redisUserService;
	
	/**
	 * 建立连接后
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session){
		String uid = session.getAttributes().get("uid").toString();
		System.out.println("用户"+uid+"已建立连接");
		SingleChatManager.getInstance().addOnlineUser(uid, session);
//		File file = new File("F:\\asd1231.mp4");
		File file = new File("F:\\output1.mp4");
		
//		MediaUtils.handleVideo("F:\\asss.mp4", "F:\\abcdd.mp4");
		FileInputStream fis = null;
        try {  
            fis = new FileInputStream(file);
            int fileSize = (int)file.length();
            int readSize = 1024000;
            System.out.println(fileSize);
            int count = fileSize%readSize==0?fileSize/readSize:fileSize/readSize+1;
            byte[] b = new byte[readSize];
            ByteBuffer buf = ByteBuffer.allocate(fileSize);
            for(int i=1;i<=count;i++){
            	if(i==count){
            		byte[] bb = new byte[fileSize%readSize];
            		fis.read(bb);
            		buf.put(bb);
//                	session.sendMessage(new BinaryMessage(bb));
            	}else{
            		fis.read(b);
            		buf.put(b);
//                	session.sendMessage(new BinaryMessage(b));
            	}
//        		break;
            }
            session.sendMessage(new BinaryMessage(buf.array()));
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{
        	if(fis!=null){
        		try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
        	}
        }
	}

	/**
	 * 消息处理
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message){
			if(message.getPayloadLength()==0)return;
			BinaryMessage vedio = (BinaryMessage)message;
			vedio.getPayload();
			Map<String,Object> msg = JacksonUtils.toObject(message.getPayload().toString(), Map.class);
			msg.put("date",DateTools.getTime());
			System.out.println("收到消息："+msg);
			SendMessageUtil.sendMessageToUser(msg.get("from")+"", msg.get("to")+"", new TextMessage(JacksonUtils.toJson(msg)));
	}

	/**
	 * 消息传输错误处理
	 */
	@Override
	public void handleTransportError(WebSocketSession session,Throwable exception){
		if (session.isOpen()) {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("错误消息");
		SingleChatManager.getInstance().removeOnlineUser(session.getAttributes().get("uid")+"");
	}

	/**
	 * 关闭连接后
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus closeStatus){
		System.out.println("用户"+session.getAttributes().get("uid")+"连接已关闭，关闭原因状态码："+closeStatus.getCode());
		SingleChatManager.getInstance().removeOnlineUser(session.getAttributes().get("uid")+"");
	}

	public boolean supportsPartialMessages() {
		return false;
	}
	
}

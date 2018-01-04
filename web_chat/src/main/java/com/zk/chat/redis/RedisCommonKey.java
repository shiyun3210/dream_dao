package com.zk.chat.redis;

public class RedisCommonKey {

	
	private static final String FLAG = "@";
	
	private static final String GYL_USER_INFO = "gyl_user_info_@";
	
	private static final String GYL_TOKEN = "g_token_@";
	
	private static final String WEBSOCKET_SESSION_KEY = "websocket_session_@";
	
	/**
	 * 用户缓存信息
	 * @param uid
	 * @return
	 */
	public static final String getUserInfo(String token){
		return GYL_USER_INFO.replaceFirst(FLAG, token);
	}
	
	/**
	 * 用户token
	 * @param uid
	 * @return
	 */
	public static final String getGToken(String uid){
		return GYL_TOKEN.replaceFirst(FLAG, uid);
	}
	
	/**
	 * websocket session 
	 * @param uid
	 * @return
	 */
	public static final String getWebsocketSessionKey(String uid){
		return WEBSOCKET_SESSION_KEY.replaceFirst(FLAG, uid);
	}
}

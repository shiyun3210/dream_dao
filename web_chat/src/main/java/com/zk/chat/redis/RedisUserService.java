package com.zk.chat.redis;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.zk.chat.entity.SessionInfo;
import com.zk.chat.util.JacksonUtils;

/**
 * 用户信息,用户token 共享
 * @author syf
 */
@Component("redisUserInfoService")
public class RedisUserService {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	
	/**
	 * 获取用户缓存信息
	 * @param uid
	 * @return
	 */
	public SessionInfo getUserInfo(String uid){
		if(StringUtils.isNotBlank(uid)){
			try {
				String obj = stringRedisTemplate.opsForValue().get(RedisCommonKey.getUserInfo(uid));
				if(obj!=null){
					return JacksonUtils.toObject(obj.toString(), SessionInfo.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 检测uid,token是否有效
	 * @param uid
	 * @param token
	 * @return
	 */
	public boolean isValidToken(String uid,String token){
		if(StringUtils.isNotBlank(uid)&&StringUtils.isNotBlank(token)){
			try {
				String result =stringRedisTemplate.opsForValue().get(RedisCommonKey.getGToken(uid)); 
				if(StringUtils.isNotBlank(result)){
					return token.equals(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
}

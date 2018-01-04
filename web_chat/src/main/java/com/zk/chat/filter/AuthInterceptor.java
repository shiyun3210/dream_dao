package com.zk.chat.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zk.chat.constants.SysConstant;
import com.zk.chat.enums.CodeMessage;
import com.zk.chat.redis.RedisUserService;
import com.zk.chat.util.RequestUtil;
/**
 * 拦截器
 * @author syf
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private RedisUserService redisUserService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		String token = RequestUtil.getCookieValue(SysConstant.USER_TOKEN_FLAG, request);
		String guid = RequestUtil.getCookieValue(SysConstant.GUID_COOKIE_NAME, request);
		if(!redisUserService.isValidToken(guid, token)){
			flushTokenError(response, CodeMessage.token_invalid);
			return false;
		}
		return true;
	}

	private void flushTokenError(HttpServletResponse res, CodeMessage message)throws IOException {

		res.setCharacterEncoding("utf-8");
		res.setContentType("application/json;charset=UTF-8");

		PrintWriter writer = res.getWriter();
		writer.append("{\"code\":" + message.getCode() + ", \"msg\":\"" + message.getMsg() + "\"}");
		writer.flush();
		writer.close();
	}
}

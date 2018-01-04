package com.zk.chat.filter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zk.chat.util.RequestUtil;
import com.zk.chat.util.ValidateString;

/**
 * 拦截器 日志记录
 * @author syf
 *
 */
@Component
public class LogInterceptor extends HandlerInterceptorAdapter {

	private final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
	private static final String USER_AGENT = "user-agent";
	private static final String ACCESS_BEGIN_TIME = "access_begin_time";
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		
		// TODO 
		String userAgent = request.getHeader(USER_AGENT);
		if (ValidateString.isNotBlank(userAgent)){
			logger.info("user-agent:" + userAgent+" IP:"+RequestUtil.getIpAddr(request));
		}
		request.setAttribute(ACCESS_BEGIN_TIME, System.currentTimeMillis());
		return true;
		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception{


	}

}
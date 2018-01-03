package com.fm.file.web.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
/**
 * 拦截器
 * @author syf
 *
 */
public class LoginFilter extends HandlerInterceptorAdapter {
	
//	private final Logger logger = LoggerFactory.getLogger(LogFilter.class);
//	private static final String USER_AGENT = "user-agent";

	public boolean preHandle(HttpServletRequest req, HttpServletResponse res,
			Object handler) throws Exception {
		boolean bool = false;
		// TODO 做访问权限控制
//		Object key = req.getAttribute("key");
//		if(key==null||key.equals("")){
//			
//			
//			return false;
//		}
		
		if(bool){
			return true;
		}else{
			flushTokenError(res, "message");
			return false;
		}
	}

	private void flushTokenError(HttpServletResponse res, String message)
			throws IOException {

		res.setCharacterEncoding("utf-8");
		res.setContentType("application/json;charset=UTF-8");

		PrintWriter writer = res.getWriter();
		writer.append("{\"code\":" + message + ", \"msg\":\"" + message + "\"}");
		writer.flush();
		writer.close();
	}
}

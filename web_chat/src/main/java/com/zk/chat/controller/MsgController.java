package com.zk.chat.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.TextMessage;

import com.zk.chat.entity.Message;
import com.zk.chat.util.DateTools;
import com.zk.chat.util.JacksonUtils;
import com.zk.chat.websocket.util.SendMessageUtil;

@Controller
public class MsgController {
	@RequestMapping(value = "vod")
	public ModelAndView vod(HttpServletRequest request) {
		return new ModelAndView("/vod/index");
	}
	@RequestMapping(value = "live")
	public ModelAndView html(HttpServletRequest request) {
		return new ModelAndView("live/index");
	}
	@RequestMapping(value = "hls")
	public ModelAndView hls(HttpServletRequest request) {
		return new ModelAndView("live/hlsindex");
	}
	
	@RequestMapping(value = "webrtc")
	public ModelAndView webRtc(HttpServletRequest request) {
		return new ModelAndView("livevideo");
	}

	/**
	 * 登录聊天
	 * @param uid
	 * @param touid
	 * @param name
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "login")
	public ModelAndView doLogin(@RequestParam int uid,@RequestParam int touid,@RequestParam String name, HttpServletRequest request) {
		request.getSession().setAttribute("uid", uid);
		request.getSession().setAttribute("name", name);
		request.getSession().setAttribute("touid", touid);
		return new ModelAndView("redirect:talk");
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "talk", method = RequestMethod.GET)
	public ModelAndView talk() {
		return new ModelAndView("talk");
	}

	// 跳转到发布广播页面
	@RequestMapping(value = "broadcast", method = RequestMethod.GET)
	public ModelAndView broadcast(HttpServletResponse response) {
		return new ModelAndView("broadcast");
	}

	// 发布系统广播（群发）
	@ResponseBody
	@RequestMapping(value = "broadcast", method = RequestMethod.POST)
	public void broadcast(String text,HttpServletResponse response) throws IOException {
		Message msg = new Message();
		msg.setDate(DateTools.getTime());
		msg.setFrom(-1);
		msg.setFromName("系统广播");
		msg.setTo(0);
		msg.setText(text);
		SendMessageUtil.sendAllUser(new TextMessage(JacksonUtils.toJson(msg)));
	}
	
}
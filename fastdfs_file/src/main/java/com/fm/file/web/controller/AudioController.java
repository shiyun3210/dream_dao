package com.fm.file.web.controller;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fm.file.util.DateTools;
import com.fm.file.util.FileUtils;
import com.fm.file.web.constants.SysConstants;
/**
 * 
 * @author syf
 */
@Controller
public class AudioController {
	
	
	/**
	 * 文件资源 加载
	 * @param path
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="audio/load")
	public void load(@RequestParam String path,HttpServletRequest request,HttpServletResponse response){
		response.setContentType("audio/mp3");
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			File file = new File(SysConstants.AUDIO_STORE_PATH+path);
			response.setContentLength((int) file.length());
			java.io.FileInputStream fis = new java.io.FileInputStream(file);
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = fis.read(b)) > 0) {
				os.write(b, 0, i);
			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				os.flush();
				os.close();
				os = null;
				response.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * 表单上传图片 multipart/form-data
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="audio/upload",method=RequestMethod.POST)
	@ResponseBody
	public Object fileUpload(HttpServletRequest request,HttpServletResponse response){
		//解决 ajax 跨域访问 请求无响应
		response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
		response.addHeader("Access-Control-Max-Age", "1728000");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/plain; charset=utf-8"); 
		long sss = System.currentTimeMillis();
		Map<String,Object> map = new HashMap<String,Object>();
		MultipartHttpServletRequest multipartRequest = null;
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
		} catch (java.lang.ClassCastException e) {
			map.put("code", "-1");
			map.put("msg", "请求方式不正确!");
		}
		if(multipartRequest!=null){
			MultipartFile file = multipartRequest.getFiles(multipartRequest.getFileNames().next()).get(0);
			if(file==null||file.isEmpty()){
				map.put("code", "-1");
				map.put("msg", "音频文件是空的");
	            return map;
			}
			String fileType = file.getOriginalFilename();
			fileType = fileType.substring(fileType.lastIndexOf(".")+1, fileType.length()).toLowerCase();
			if(!fileType.equals("amr")&&!fileType.equals("mp3")) {
				map.put("code", "-1");
				map.put("msg", "不支持的音频文件格式");
	            return map;
	        }
			map.put("code", "0");
			map.put("msg", "处理成功");
			map.put("data", responseResult(file,fileType));
			System.out.println("耗时："+(System.currentTimeMillis()-sss));
			return map;
		}
		map.put("code", "-1");
		map.put("msg", "没有接收到任何数据!");
		return map;
	}
	
	public static String responseResult(MultipartFile file,String fileType){
		Date date = new Date();
		final String audioShortPath = generateAudioShortPath(date);
		final String audioName = generateAudioName(date);
		final String audioTempFullPath = SysConstants.FILE_TEMP_PATH+audioShortPath+audioName+"."+fileType;
		try {
			FileUtils.createDirs(SysConstants.FILE_TEMP_PATH+audioShortPath, true);
			FileUtils.createDirs(SysConstants.AUDIO_STORE_PATH+audioShortPath, true);
			FileUtils.copyFile(file.getInputStream(), audioTempFullPath, true);
			handleAudio(audioTempFullPath,SysConstants.AUDIO_STORE_PATH+audioShortPath+audioName+".mp3");
			FileUtils.deleteFile(audioTempFullPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return audioShortPath+audioName+".mp3";
	}
	
	/**
	 * 生成图片存储路径   \yymmdd\
	 * @return
	 */
	private static String generateAudioShortPath(Date date){
		return DateTools.formatDateTime(date, "yyyyMMdd")+File.separator;
	}
	
	private static String generateAudioName(Date date){
		return DateTools.formatDateTime(date, "yyyyMMddHHmmss")+(Math.random()+"").substring(2, 8);
	}
	
	public static void handleAudio(String sourceFile,String targetFile){
		File file = new File(sourceFile);
		if(file.isFile()){
			AudioAttributes audioAtr = new AudioAttributes();
			audioAtr.setBitRate(new Integer(128000));
			audioAtr.setChannels(new Integer(2));
			audioAtr.setSamplingRate(new Integer(11025));
			audioAtr.setVolume(new Integer(100));
			audioAtr.setCodec("libmp3lame");
			
			try {
				Encoder encoder = new Encoder();
//				MultimediaInfo mediaInfo = encoder.getInfo(file);
				
//				AudioInfo audioInfo = mediaInfo.getAudio();
				
//				System.out.println("时长："+mediaInfo.getDuration());
				EncodingAttributes attrs = new EncodingAttributes();  
				attrs.setFormat("mp3");  
				attrs.setAudioAttributes(audioAtr);
//				attrs.setDuration(new Float(1000.5));
//				attrs.setOffset(new Float(0.00));
				encoder.encode(file, new File(targetFile), attrs);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InputFormatException e) {
				e.printStackTrace();
			} catch (EncoderException e) {
				//捕获不输出
				System.out.println("捕获");
			}
		}
	}
}

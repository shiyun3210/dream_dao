package com.fm.file.web.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fm.file.util.DateTools;
import com.fm.file.util.FileUtils;
import com.fm.file.web.constants.SysConstants;

/**
 * 
 * @author syf
 */
@Scope("prototype")
@Controller
public class FileResourceLoadController {
	
	private Logger logger = LoggerFactory.getLogger(FileResourceLoadController.class);
	
	/**
	 * 文件打包下载
	 * @param zipname
	 * @param filespath
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="resource/packload")
	public void packload(@RequestParam(required=false,defaultValue="")String zipname,@RequestParam String filespath,HttpServletRequest request,HttpServletResponse response){
		if("".equals(zipname)){
			zipname = DateTools.formatChinesesDateTime(new Date())+"下载的附件";
		}
		zipname = zipname+".zip";
		try {
			zipname = URLEncoder.encode(zipname,"UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		response.setHeader("Content-disposition", "attachment; filename="+ zipname);
		if(!filespath.equals("")){
			String[] filesPathArray = filespath.split(",");
			File[] files = new File[filesPathArray.length];

			for (int i = 0; i < filesPathArray.length; i++) {
				files[i] = new File(SysConstants.RES_ROOT_PATH
						+ filesPathArray[i]);
			}
			String tempTime = DateTools.formatNumberDateTime(new Date())
					+ new Random().nextInt(60000);
			String tmpFileName = tempTime + "temp.zip";
			byte[] buffer = new byte[1024];
			String strZipPath = SysConstants.RES_ROOT_PATH + tmpFileName;

			ZipOutputStream out = null;
			try {
				out = new ZipOutputStream(new FileOutputStream(strZipPath));
				for (int i = 0; i < files.length; i++) {
					try {
						FileInputStream fis = new FileInputStream(files[i]);
						out.putNextEntry(new ZipEntry(files[i].getName()));
						// 设置压缩文件内的字符编码，不然会变成乱码
						out.setEncoding("UTF-8");
						int len;
						// 读入需要下载的文件的内容，打包到zip文件
						while ((len = fis.read(buffer)) > 0) {
							out.write(buffer, 0, len);
						}
						out.closeEntry();
						fis.close();
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 返回到客户端
			FileInputStream fis = null;
			OutputStream os = null;
			try {
				os = response.getOutputStream();
				File file = new File(strZipPath);
				if(file!=null&&file.length()>0){
					response.setContentLength((int) file.length());
					fis = new java.io.FileInputStream(file);
					byte[] b = new byte[1024];
					int i = 0;
					while ((i = fis.read(b)) > 0) {
						os.write(b, 0, i);
					}
					// 删除临时文件
					file.delete();
				}else{
					logger.info("文件信息异常："+strZipPath+",File:"+file);
				}
			} catch (FileNotFoundException e) {
				logger.info("文件不存在：" + strZipPath);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (os != null) {
						os.flush();
						os.close();
						os = null;
					}
					response.flushBuffer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/**
	 * 文件资源 加载
	 * @param path
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="resource/load")
	public void load(@RequestParam String path,@RequestParam(required=false,defaultValue="0")int isDownload,@RequestParam(required=false,defaultValue="")String rename,
			HttpServletRequest request,HttpServletResponse response){
		String fileType = FileUtils.getFileType(path);
		String fileName = FileUtils.getFileName(path);
		if(fileType.equals("image")&&isDownload==0){
			//是否修改
        	String modified=request.getHeader("If-Modified-Since");
        	//图片路径
        	String origPath = SysConstants.RES_ROOT_PATH +path;
        	if(!StringUtils.isEmpty(modified)){

        		DateFormat format1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.UK);
        		format1.setTimeZone(TimeZone.getTimeZone("GMT"));
        		try {
					long time = format1.parse(modified).getTime();
					
					//TODO 此处更新的条件：如果内容有更新，应该重新返回内容最新修改的时间戳 
//					long lastModifiedFile = FileUtils.getModifiedTime(origPath);
					long lastModifiedSys = System.currentTimeMillis();
					
					if(time>lastModifiedSys){
						this.outWrite(1,isDownload, origPath, response,fileName,fileType,"");
					}else{
						this.outWrite(2,isDownload, origPath, response,fileName,fileType,"");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        			
        	}else{
        		//读取文件
        		File datu = new File(origPath);
        		if (datu != null && datu.exists() && !datu.isDirectory()&& datu.canRead()) {
        			this.outWrite(2,isDownload, origPath, response,fileName,fileType,"");
        		}
        	}
        	
        }else{
        	//下载文件
        	outWrite(2,isDownload,SysConstants.RES_ROOT_PATH+path, response, fileName,fileType,rename);
        }
	}
	
	
	/**
	 * 
	 * @param filepath
	 * @param response
	 * @param filename
	 */
	private void outWrite( int outWriteType,int isDownload,final String filepath,final HttpServletResponse response, String filename,String fileType,String rename){
		// 图片
		if (fileType.equals("image")&&isDownload==0) {

			Date d = DateTools.nDaysAfterOneDate(new Date(), 5);
			DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.UK);

			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			String tt = format.format(d);
			//1 无修改 拿缓存
			if(outWriteType==1){//
				response.setContentType("image/jpeg");
				response.setHeader("Date", tt);
				response.setHeader("Cache-Control", "max-age="+SysConstants.RESURCE_EXPIRE);
				response.setHeader("Status Code", "304");
				response.setStatus(304);
			}else{//第一次请求  设置页面缓存
				OutputStream os = null;
				FileInputStream fis = null;
				try {
					os = response.getOutputStream();
					File file = new File(filepath);
					if(file!=null&&file.length()>0){
						response.setContentType("image/jpeg");
						response.setHeader("Last-Modified", tt);
						response.setHeader("Date", tt);
						response.setHeader("Cache-Control", "max-age=" + SysConstants.RESURCE_EXPIRE);
						response.setContentLength((int) file.length());
						fis = new java.io.FileInputStream(file);
						byte[] b = new byte[1024];
						int i = 0;
						while ((i = fis.read(b)) > 0) {
							os.write(b, 0, i);
						}
					}else{
						logger.info("文件信息异常："+filepath+",File:"+file);
					}
				}catch(FileNotFoundException e){
					logger.info("文件不存在："+filepath);
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						if(fis!=null){
							fis.close();
						}
						if(os!=null){
							os.flush();
							os.close();
							os = null;
						}
						response.flushBuffer();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}

		}else if(isDownload==1){//文件下载
			try {
				if(org.apache.commons.lang.StringUtils.isNotBlank(rename)){
					filename = URLEncoder.encode(rename+FileUtils.getExtension(filename),"UTF8");
				}else{
					filename = URLEncoder.encode(filename,"UTF8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			response.setHeader("Content-disposition", "attachment; filename=" + filename);
			OutputStream os = null;
			FileInputStream fis = null;
			try {
				os = response.getOutputStream();
				File file = new File(filepath);
				if(file!=null&&file.length()>0){
					response.setContentLength((int) file.length());
					fis = new java.io.FileInputStream(file);
					byte[] b = new byte[1024];
					int i = 0;
					while ((i = fis.read(b)) > 0) {
						os.write(b, 0, i);
					}
				}else{
					logger.info("文件信息异常："+filepath+",File:"+file);
				}
			}catch(FileNotFoundException e){
				logger.info("文件不存在："+filepath);
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(fis!=null){
						fis.close();
					}
					if(os!=null){
						os.flush();
						os.close();
						os = null;
					}
					response.flushBuffer();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
}

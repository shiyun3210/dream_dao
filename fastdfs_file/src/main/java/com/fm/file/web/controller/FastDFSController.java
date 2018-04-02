package com.fm.file.web.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.simpleimage.ImageWrapper;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.util.ImageReadHelper;
import com.fm.file.util.DateTools;
import com.fm.file.util.FileUtils;
import com.fm.file.web.constants.SysConstants;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.GenerateStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;

@Scope(value="prototype")
@Controller
public class FastDFSController {
	
	
	/**
	 * 图片 缩放比例  宽高 区间
	 */
	private static final int IMG_SCALE_WH_INTERVAL = 20;
	/**
	 * 当前jgp 图片 输出质量，质量越高，图片越大
	 */
	private static final double IMG_QUALITY = 0.8d;
	
	/**
	 * 当前jgp 图片 输出质量，质量越高，图片越大
	 */
	private static final double IMG_QUALITY_09 = 1.0d;
	
	private static final double DEFAULT_SPEC = 1200;
	
	@Autowired
	private GenerateStorageClient generateStorageClient;
	@Autowired
	private TrackerClient trackerClient;
	
//	@RequestMapping(value="tempUpload",method=RequestMethod.POST)
//	public @ResponseBody Object tempUpload(
//			@RequestParam(required=false,defaultValue="0") int scalewidth,@RequestParam(required=false,defaultValue="0") int scaleheight,@RequestParam(required=false,defaultValue="0") int iswatermark,
//			HttpServletRequest request,HttpServletResponse response){
//		//解决 ajax 跨域访问 请求无响应
//		response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
//		response.addHeader("Access-Control-Max-Age", "1728000");
//		response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		response.addHeader("Content-Type","text/json"); 
//		Map<String,Object> map = new HashMap<String, Object>();
//			List<Map<String,Object>> listFileMap = new ArrayList<Map<String,Object>>();
//			try {
//				Map<String,Object> fileMap = null;
//				String originalFilename = null;
//				fileMap = new HashMap<String, Object>();
//				byte[] ins = scalingImage(request.getInputStream(), scalewidth, scaleheight,iswatermark);
//				if(ins!=null&&ins.length>0){
//					StorePath resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(), new ByteArrayInputStream(ins), ins.length, "jpg");
//					if(resultFile!=null){
//						fileMap.put("name", "");
//						fileMap.put("originalFilename", originalFilename);
//						fileMap.put("contendType","");
//						fileMap.put("url", resultFile.getFullPath());
//						fileMap.put("path", SysConstants.RESURCE_SERVER_URL+resultFile.getFullPath());
//					}
//					listFileMap.add(fileMap);
//				}
//					
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			if(!listFileMap.isEmpty()){
//				map.put("data", listFileMap);
//				map.put("code", "0");
//				map.put("msg", "ok");
//				return map;
//			}
//	    map.put("code", "-1");
//	    map.put("msg", "系统繁忙");
//		return map;
//	}
	
	
	/**
	 * 文件上传
	 * @param uptype 1 form表单、插件上传  2 参数型图片上传
	 * @param imgData 图片数据（参数图片上传 使用）
	 * @param filetype 文件类型（参数图片上传 使用）
	 * @param filename 文件名称（参数图片上传 使用）
	 * @param scalewidth 压缩目标尺寸 宽度
	 * @param scaleheight 压缩目标尺寸 高度
	 * @param iswatermark 图片是否加水印 0 否 1 是
	 * @param isorgin 是否原图上传 0 否 1 是
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="resource/upload",method=RequestMethod.POST)
	public @ResponseBody Object fastDFSUploadFile(@RequestParam(required=false,defaultValue="1") int uptype,@RequestParam(required=false,defaultValue="") String imgData,
			@RequestParam(required=false,defaultValue="") String filetype,@RequestParam(required=false,defaultValue="") String filename,
			@RequestParam(required=false,defaultValue="0") int scalewidth,@RequestParam(required=false,defaultValue="0") int scaleheight,
			@RequestParam(required=false,defaultValue="0") int iswatermark,@RequestParam(required=false,defaultValue="0") int isorgin,
			HttpServletRequest request,HttpServletResponse response){
		//解决 ajax 跨域访问 请求无响应
		response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
		response.addHeader("Access-Control-Max-Age", "1728000");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Content-Type","text/json"); 
		Map<String,Object> map = new HashMap<String, Object>();
		
		//文件上传限制
		if(request.getContentLength()/1024/1024>50){
			map.put("code", "-1");
			map.put("msg", "文件大小超出限制");
		}
		if(uptype==1){
			MultipartHttpServletRequest multipartRequest = null;
			try {
				multipartRequest = (MultipartHttpServletRequest) request;
			} catch (java.lang.ClassCastException e) {
				map.put("code", "-1");
				map.put("msg", "请求方式不正确");
				return map;
			}
			List<MultipartFile> listFile = multipartRequest.getFiles(multipartRequest.getFileNames().next());
			if(listFile==null||listFile.isEmpty()){
				map.put("code", "-1");
			    map.put("msg", "上传文件内容为空");
				return map;
			}
			List<Map<String,Object>> listFileMap = new ArrayList<Map<String,Object>>();
			try {
				Map<String,Object> fileMap = null;
				String originalFilename = null;
				for(MultipartFile file : listFile){
					fileMap = new HashMap<String, Object>();
					originalFilename = file.getOriginalFilename();
					String fileType = FileUtils.getFileType(originalFilename);
					if(fileType.equals("image")){
						String fileExtName = "jpg";
						byte[] ins = scalingImage(file.getInputStream(), scalewidth, scaleheight,iswatermark,isorgin,fileExtName);
						if(ins!=null&&ins.length>0){
							StorePath resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(), new ByteArrayInputStream(ins), ins.length,fileExtName);
							if(resultFile!=null){
								fileMap.put("name", file.getName());
								fileMap.put("originalFilename", originalFilename);
								fileMap.put("contendType", file.getContentType());
								fileMap.put("url", resultFile.getFullPath());
								fileMap.put("path", SysConstants.RESURCE_SERVER_URL+resultFile.getFullPath());
							}
							listFileMap.add(fileMap);
						}
					}else{
						StorePath resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(), file.getInputStream(), file.getSize(), 
								FileUtils.getExtensionNotDot(originalFilename));
						if(resultFile!=null){
							fileMap.put("name", file.getName());
							fileMap.put("originalFilename", originalFilename);
							fileMap.put("contendType", file.getContentType());
							fileMap.put("url", resultFile.getFullPath());
							fileMap.put("path", SysConstants.RESURCE_SERVER_URL+resultFile.getFullPath());
						}
						listFileMap.add(fileMap);
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(!listFileMap.isEmpty()){
				map.put("data", listFileMap);
				map.put("code", "0");
				map.put("msg", "ok");
				return map;
			}
		}else{
			if(StringUtils.isNotBlank(imgData)&&imgData.indexOf("data:image/jpeg;base64,")!=-1){
				imgData = imgData.replace("data:image/jpeg;base64,", "");
				sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
				// Base64解码
				try {
					byte[] bytes = decoder.decodeBuffer(imgData);
					InputStream is = new ByteArrayInputStream(bytes);
					if("".equals(filetype)){
						filetype = "jpg";
					}
					byte[] ins = scalingImage(is, scalewidth, scaleheight,iswatermark,isorgin,filetype);
					if(ins!=null&&ins.length>0){
						StorePath resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(), new ByteArrayInputStream(ins), ins.length, filetype);
						if(resultFile!=null){
							List<Map<String,Object>> listFileMap = new ArrayList<Map<String,Object>>();
							Map<String,Object> fileMap = new HashMap<String, Object>();
							fileMap.put("name", "");
							fileMap.put("originalFilename", filename);
							fileMap.put("contendType", "");
							fileMap.put("url", resultFile.getFullPath());
							fileMap.put("path", SysConstants.RESURCE_SERVER_URL+resultFile.getFullPath());
							listFileMap.add(fileMap);
							map.put("data", listFileMap);
							map.put("code", "0");
							map.put("msg", "ok");
							return map;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
			    map.put("code", "-1");
			    map.put("msg", "参数型图片上传参数为空");
				return map;
			}
		}
	    map.put("code", "-1");
	    map.put("msg", "系统繁忙");
		return map;
	}
	
	/**
	 * 等比例缩放
	 * @param in
	 * @param scaleWidth
	 * @param scaleHeight
	 * @return
	 */
	private static byte[] scalingImage(InputStream in,int scaleWidth,int scaleHeight,int iswatermark,int isorgin,String fileExtName){
		try {
//			BufferedImage sourceImg = ImageIO.read(in);
			int height = 0;
	        int width = 0;
	        BufferedImage sourceImg = null;
			try {
				ImageWrapper imageWrapper = ImageReadHelper.read(in);
				height = imageWrapper.getHeight();
				width = imageWrapper.getWidth();
				sourceImg = imageWrapper.getAsBufferedImage();
			} catch (SimpleImageException e) {
				e.printStackTrace();
			}
	        // 在内存当中生成缩略图
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        double spec = 0;
	        double scaleSize = 0;
	        if(isorgin==1){
	        	scaleSize = 0;
	        }else if(scaleWidth==0&&scaleHeight==0){
	        	if((width/height)>3){
	        		spec = scaleHeight;
		        	if(Math.abs(spec-height)>IMG_SCALE_WH_INTERVAL&&height>spec){
		        		scaleSize = new BigDecimal(spec/((double)height)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
					}
	        	}else if((height/width)>3){
	        		spec = scaleWidth;
		        	if(Math.abs(spec-width)>IMG_SCALE_WH_INTERVAL&&width>spec){
		        		scaleSize = new BigDecimal(spec/((double)width)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
		        	}
	        	}else{
	        		spec = DEFAULT_SPEC;
		        	if((Math.abs(spec-width)>IMG_SCALE_WH_INTERVAL&&width>spec)||(Math.abs(spec-height)>IMG_SCALE_WH_INTERVAL&&height>spec)){
		        		scaleSize = new BigDecimal(spec/(double)(width>height?width:height)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
					}
	        	}
	        }else if(scaleWidth==0){
	        	spec = scaleHeight;
	        	if(Math.abs(spec-height)>IMG_SCALE_WH_INTERVAL&&height>spec){
	        		scaleSize = new BigDecimal(spec/((double)height)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
				}
	        }else if(scaleHeight==0){
	        	spec = scaleWidth;
	        	if(Math.abs(spec-width)>IMG_SCALE_WH_INTERVAL&&width>spec){
	        		scaleSize = new BigDecimal(spec/((double)width)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
	        	}
	        }else{
        		spec = scaleWidth;
	        	if((Math.abs(spec-width)>IMG_SCALE_WH_INTERVAL&&width>spec)||(Math.abs(spec-height)>IMG_SCALE_WH_INTERVAL&&height>spec)){
	        		scaleSize = new BigDecimal(spec/(double)(width>height?width:height)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
				}
	        }
	        if(scaleSize==0){
	        	if(iswatermark==1){
	        		File wmFile = new File(SysConstants.WATERMARK_IMG_CN_PATH);
	        		Thumbnails.of(sourceImg).size(width, height).watermark(Positions.CENTER, ImageIO.read(wmFile), 1f).outputFormat(fileExtName).outputQuality(IMG_QUALITY_09).toOutputStream(out);
	        	}else{
	        		Thumbnails.of(sourceImg).size(width, height).outputFormat(fileExtName).outputQuality(IMG_QUALITY_09).toOutputStream(out);
	        	}
	        }else {
	        	if(iswatermark==1){
	        		File wmFile = new File(SysConstants.WATERMARK_IMG_CN_PATH);
	        		Thumbnails.of(sourceImg).scale(scaleSize).watermark(Positions.CENTER, ImageIO.read(wmFile), 1f).outputFormat(fileExtName).outputQuality(IMG_QUALITY).toOutputStream(out);
	        	}else{
	        		Thumbnails.of(sourceImg).scale(scaleSize).outputFormat(fileExtName).outputQuality(IMG_QUALITY).toOutputStream(out);
	        	}
	        }
	        return out.toByteArray();
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
//	@RequestMapping(value="delfiles")
//	public @ResponseBody Object delFiles(@RequestParam String delfiles,HttpServletRequest request,HttpServletResponse response){
//		Map<String,Object> map = new HashMap<String,Object>();
//		try {
//			for(String str :delfiles.split(",")){
//				generateStorageClient.deleteFile(str.substring(0,str.indexOf("/")), str.substring(str.indexOf("/")+1,str.length()));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	    map.put("code", "0");
//	    map.put("msg", "ok");
//		return map;
//		
//	}
	
	
//	@RequestMapping(value="updatefile",method=RequestMethod.POST)
//	public @ResponseBody Object updateFile(@RequestParam String oldfilepath,HttpServletRequest request,HttpServletResponse response){
//		//解决 ajax 跨域访问 请求无响应
//		response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
//		response.addHeader("Access-Control-Max-Age", "1728000");
//		response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		response.addHeader("Content-Type","text/json"); 
//		Map<String,Object> map = new HashMap<String, Object>();
//		
//		//文件上传限制
//		if(request.getContentLength()/1024/1024>50){
//			map.put("code", "-1");
//			map.put("msg", "文件大小超出限制");
//		}
//		MultipartHttpServletRequest multipartRequest = null;
//		try {
//			multipartRequest = (MultipartHttpServletRequest) request;
//		} catch (java.lang.ClassCastException e) {
//			map.put("code", "-1");
//			map.put("msg", "请求方式不正确");
//			return map;
//		}
//		List<MultipartFile> listFile = multipartRequest.getFiles(multipartRequest.getFileNames().next());
//		if(listFile==null||listFile.isEmpty()){
//			map.put("code", "-1");
//		    map.put("msg", "上传文件内容为空");
//			return map;
//		}
//		List<Map<String,Object>> listFileMap = new ArrayList<Map<String,Object>>();
//		try {
//			generateStorageClient.deleteFile(oldfilepath.substring(0,oldfilepath.indexOf("/")), oldfilepath.substring(oldfilepath.indexOf("/")+1,oldfilepath.length()));
//			Map<String,Object> fileMap = new HashMap<String, Object>();
//			MultipartFile file = listFile.get(0);
//			StorePath resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(), file.getInputStream(), file.getSize(), "jpg");
//			if(resultFile!=null){
//				fileMap.put("name", file.getName());
//				fileMap.put("originalFilename", file.getOriginalFilename());
//				fileMap.put("contendType", file.getContentType());
//				fileMap.put("url", resultFile.getFullPath());
//				fileMap.put("path", SysConstants.RESURCE_SERVER_URL+resultFile.getFullPath());
//			}
//			listFileMap.add(fileMap);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if(!listFileMap.isEmpty()){
//			map.put("data", listFileMap);
//			map.put("code", "0");
//			map.put("msg", "ok");
//			return map;
//		}
//	    map.put("code", "-1");
//	    map.put("msg", "系统繁忙");
//		return map;
//		
//	}
	
	/**
	 * 文件下载 
	 * @param filepath 文件路径
	 * @param extname 扩展名
	 * @param filename 下载文件名称
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="download")
	public void downloadFile(String filepath,String extname,@RequestParam(required=false,defaultValue="") String filename,HttpServletRequest request,HttpServletResponse response){
		if(StringUtils.isNotBlank(filename)){
			try {
				filename = URLEncoder.encode(filename+"."+extname,"UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			filename = tempFilename()+"."+extname;
		}
		response.reset();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		response.setHeader("Content-disposition", "attachment; filename="+filename);
		try {
			responseContent(generateStorageClient.downloadFile(filepath.substring(0,filepath.indexOf("/")), filepath.substring(filepath.indexOf("/")+1,filepath.length()),new DownloadByteArray()), response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String tempFilename(){
		return DateTools.getTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
	}
	
	private static void responseContent(byte[] fileContent,HttpServletResponse response){
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(fileContent);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}

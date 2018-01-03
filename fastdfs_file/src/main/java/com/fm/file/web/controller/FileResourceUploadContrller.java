package com.fm.file.web.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fm.file.util.DateTools;
import com.fm.file.util.DateUtils;
import com.fm.file.util.FileUtils;
import com.fm.file.util.ImageUtils;
import com.fm.file.util.JacksonUtils;
import com.fm.file.web.constants.SysConstants;

/**
 * 
 * @author syf
 */
//@Scope(value="prototype")
//@Controller
public class FileResourceUploadContrller {
	
//	public static void main(String[] args) {
//		String[] writeIps = "192.168.10|192.168.1".split("\\|");
//		System.out.println(writeIps[0]);
//	}
	
	/**
	 * 文件上传
	 * @param utype 1 插件上传  2 参数型图片上传
	 * @param callback
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping(value="resource/upload",method=RequestMethod.POST)
	public @ResponseBody Object uploadFile(@RequestParam(required=false,defaultValue="1") int uptype,
			@RequestParam(required=false,defaultValue="") String folder,
			@RequestParam(required=false,defaultValue="") String filepath,@RequestParam(required=false,defaultValue="") String filename,
			@RequestParam(required=false,defaultValue="") String callback,@RequestParam(required=false,defaultValue="") String imgData,
			@RequestParam(required=false,defaultValue="0") int w,@RequestParam(required=false,defaultValue="0") int h,
			@RequestParam(required=false,defaultValue="") String fileType,
			HttpServletRequest request,HttpServletResponse response){
		
		//解决 ajax 跨域访问 请求无响应
		response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
		response.addHeader("Access-Control-Max-Age", "1728000");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		if(!StringUtils.isEmpty(callback)){
			response.addHeader("Content-Type","application/javascript");
		}else{
			response.addHeader("Content-Type","text/json");
		}
		Map<String,Object> map = new HashMap<String, Object>();
		//请求IP限制
//		String reqIp = RequestUtil.getIpAddr(request);
//		reqIp = reqIp.substring(0, reqIp.lastIndexOf("."));
//		boolean bool = false;
//		for(String ipWrite : SysConstants.IP_WRITES){
//			if(reqIp.equals(ipWrite)){
//				bool = true;
//			}
//		}
//		if(!bool){
//			map.put("code", "-1");
//			map.put("error", "非法请求ip");
//			return this.buildJson(map,callback);
//		}
		if(uptype!=1&&uptype!=2){
			map.put("code", "-1");
			map.put("error", "不支持的文件上传方式");
			return this.buildJson(map,callback);
		}
		//文件上传限制
		if(request.getContentLength()/1024/1024>10){
			map.put("code", "-1");
			map.put("msg", "文件大小超出限制");
			return buildJson(map, callback);
		}
		if(uptype==1){
			MultipartHttpServletRequest multipartRequest = null;
			try {
				multipartRequest = (MultipartHttpServletRequest) request;
			} catch (java.lang.ClassCastException e) {
				map.put("code", "-1");
				map.put("msg", "请求方式不正确");
				return buildJson(map, callback);
			}
	        List<FileResource> listResource = new ArrayList<FileResource>();
			List<MultipartFile> listFile = multipartRequest.getFiles(multipartRequest.getFileNames().next());
			for(MultipartFile file : listFile){
				 FileResource resource=this.uploadFile(file,folder,filepath,filename);
				 listResource.add(resource);
				 if(resource==null){
					map.put("code", "-1");
	        	 	map.put("msg", "文件存储失败");
	        	 	return map;
				 }
			}
			 if(!listResource.isEmpty()){
	        	map.put("code", "0");
				map.put("msg", "ok");
				map.put("data",listResource);
	        	return this.buildJson(map,callback);
	         }
			 map.put("code", "-1");
			 map.put("msg", "上传文件内容为空");
			 return map;	
			 
		}else{//参数型 上传图片
			Date date = new Date();
			
			String fileName = "";
			String imgFilePath = "";
			if(!filename.equals("")&&!filepath.equals("")){
				fileName = filename;
				imgFilePath = filepath;
			}else{
				imgFilePath = new StringBuilder().append(SysConstants.FILE_PATH).append(File.separator)
						.append(DateTools.formatDateTime(date, "yyyyMMdd")).append("").append(File.separator).toString();
				fileName = new StringBuilder().append(DateTools.formatDateTime(date, "yyyyMMddHHmmss")+(Math.random()
	    				+"").substring(2, 8)).append(".jpg").toString();
			}
			boolean bools = false;
			try {
				bools = generateParamsImage(imgData, SysConstants.RES_ROOT_PATH+File.separator+imgFilePath+File.separator,fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(bools){
				FileResource resource=new FileResource();
	            resource.setSuffix(fileType);
	            resource.setUrl(imgFilePath+fileName);
	            resource.setPath(SysConstants.IMG_RESOURCE_LOAD+imgFilePath);
	            
	            resource.setTitle("");
	            resource.setSize(0);
	           
	            //(1:图片，2：网页，3视频,4音频)
	            if(fileType.equals("image")){
	            	resource.setType(1);
	            }
	            map.put("code", "0");
				map.put("msg", "ok");
				map.put("data",resource);
	        	return this.buildJson(map,callback);
			}
			map.put("code", "-1");
        	map.put("msg", "文件存储失败");
			return this.buildJson(map,callback); 
		}
//		else{//字节流
//			
//			List<FileResource> resources=new ArrayList<FileResource>();
//			HashMap<String, Object> result = new HashMap<String, Object>();
//			//取getInputStream
//			ServletInputStream inStream=null;
//			 
//			try {
//				inStream = request.getInputStream();
//				resources = saveFile(inStream, request, folder);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			if (!resources.isEmpty()) {
//				result.put("code", "0");
//				result.put("msg", "ok");
//				result.put("data", resources.get(0));
//				return this.buildJson(result, callback);
//			} else {
//				result.put("code", "-1");
//				result.put("msg", "文件上传失败");
//				return this.buildJson(result, callback);
//			}
//			
//		}
		
	}
	
	/**
	 *  参数型 数据 图片
	 * @param imgStr
	 * @param imgFilePath
	 * @return
	 */
	public static boolean generateParamsImage(String imgData, String imgFilePath,String fileName) {
		if (imgData == null) // 图像数据为空
			return false;
		OutputStream out =null;
		imgData = imgData.replace("data:image/jpeg;base64,", "");
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		try {
			// Base64解码
			byte[] bytes = decoder.decodeBuffer(imgData);
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
			FileUtils.createDirs(imgFilePath,true);
			// 生成jpeg图片
			out = new FileOutputStream(imgFilePath+fileName);
			out.write(bytes);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			if(out!=null){
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	 /**
     * 单文件上传，返回文件存放的路径
     *
     * @param file 文件
     * @param path 文件存放路径
     * @return 文件存放的 URL
     */
    protected FileResource uploadFile(MultipartFile file,String folder,String filepath,String filename) {
        if (file != null && !file.isEmpty()) {
          
            String fileType = FileUtils.getFileType(file.getOriginalFilename());
            
            try {
            	String filePath = "";
            	if(!filepath.equals("")&&!filename.equals("")){
            		filePath = filepath+"/"+filename;
            	}else{
            		filePath = this.generateNewPath(file,folder);
            	}
                String absPath= SysConstants.RES_ROOT_PATH +filePath;
                // 创建文件
            	FileUtils.copyFile(file.getInputStream(),absPath , true); 
              
                FileResource resource=new FileResource();
                filePath = filePath.replace("//", "/");
                resource.setSuffix(FileUtils.getExtensionNotDot(filePath));
                resource.setUrl(filePath);
                resource.setPath(SysConstants.IMG_RESOURCE_LOAD+filePath);
                
                resource.setTitle(file.getOriginalFilename());
                resource.setSize((int)(file.getSize()/1024));
               
                //(1:图片，2：网页，3视频,4音频)
                if(fileType.equals("image")){
                	resource.setType(1);
                }
                return resource;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
	
	/**
	 * 生成新地址
	 * @param file
	 * @return
	 */
	public String generateNewPath(MultipartFile file,String folder){
		 String spec = "";
		 Date date = new Date();
         String fileType = FileUtils.getFileType(file.getOriginalFilename());
         String filePath = "";
         String f= "";
         if(StringUtils.isEmpty(folder)){
        	 f= SysConstants.FILE_PATH+File.separator+folder+File.separator+DateTools.formatDateTime(date, "yyyyMMdd");
         }else{
        	 f= SysConstants.FILE_PATH+File.separator+DateTools.formatDateTime(date, "yyyyMMdd");
         }
         if (fileType.equals("image")) {
             try {
                 spec = "_" + ImageUtils.getSpec(file.getInputStream()); // 宽x高
             } catch (IOException e) {
                 e.printStackTrace();
             }
             
    		filePath = new StringBuilder().append(f).append(File.separator).append(DateTools.formatDateTime(date, "yyyyMMddHHmmss")+(Math.random()
    				+"").substring(2, 8)).append(spec).append(FileUtils.getExtension(file.getOriginalFilename())).toString().replaceAll("\\\\", "/");
         }else{
        	 filePath = new StringBuilder().append(f).append(File.separator).append(DateTools.formatDateTime(date, "yyyyMMddHHmmss")+(Math.random()
     				+"").substring(2, 8)).append(FileUtils.getExtension(file.getOriginalFilename())).toString();
         }
		return filePath;
	}
	
	/**
	 * 根据callback参数组装返回的JSON，解决跨域问题
	 * 
	 * @param obj
	 * @param callback
	 * @return String
	 */
	protected Object buildJson(Map<String,Object> obj, String callback) {
		String json = null;
		if (callback != null && !callback.isEmpty()) {
			json = JacksonUtils.toJsonP(obj, callback);
		} else {
			return obj;
		}
		return json;
	}
	/**
	 * 字节流 上传
	 * @param inStream
	 * @param request
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public List<FileResource>  saveFile(InputStream inStream,HttpServletRequest request,String folder) throws Exception{
		
		List<FileResource> resources=new ArrayList<FileResource>();
		
		int size = request.getContentLength(); // 取HTTP请求流长度
        byte[] buffer = new byte[1024]; // 用于缓存每次读取的数据
        byte[] result = new byte[size]; // 用于存放结果的数组
        int count = 0;
        int rbyte = 0;
        // 循环读取
        while (count < size)
        {
            try {
				rbyte = inStream.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			} // 每次实际读取长度存于rbyte中 sflj
            for (int i = 0; i < rbyte; i++)
            {
                result[count + i] = buffer[i];
            }
            count += rbyte;
        }
        

        // 先找到文件名和图片流的标志位'|'
        int index = 0;
        for (int i = 0; i < result.length; i++)
        {
            byte b = result[i];
            if (b == '|')
            {
                index = i;
                break;
            }
        }

        // 存放文件名
        byte name[] = new byte[index + 1];
        // 存放图片字节
        byte[] img = new byte[size - index];
        for (int i = 0; i < result.length; i++)
        {
            if (i < index)
            {
                name[i] = result[i];
            }
            if (i > index)
            {
                // 这时注意img数组的index要从0开始
                img[i - index - 1] = result[i];
            }
        }
        // 还原文件名
        String fileName =new String(name);
		try {
			fileName = new String(name,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

        try {
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

     
        
     
        //上传错误中止后续操作
        if(fileName.length()<= 1)
        {
          //  return resources;
        }
        //"E:\\images\\_file\\newFil1e.jpg";//
        String fileType = FileUtils.getFileType(fileName);
        String newFilePath =this.generateNewPath(fileName, folder);
        String destFilename="";
       
        destFilename=SysConstants.RES_ROOT_PATH+File.separator+newFilePath;
        
        
      //  Constants.RES_ROOT_PATH+File.separator + this.generateNewPath(newFileName);
       
      //  FileUtils.writeFile(new String(img), destFilename, "utf-8", append)
       
        try {
			FileUtils.copyFile(new ByteArrayInputStream(img), destFilename, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        fileType = FileUtils.getFileType(newFilePath);
        
        FileResource resource=new FileResource();
        newFilePath = newFilePath.replace("//", "/");
        resource.setTitle(fileName);
        resource.setSize((int)(size/1024));
        resource.setPath(SysConstants.IMG_RESOURCE_LOAD+newFilePath);//
        resource.setUrl(newFilePath);
        //(1:图片，2：网页，3视频,4音频)
        if(fileType.equals("image")){
        	resource.setType(1);
        }
        
        resources.add(resource);
       
        return resources;
	}
	
	/**
	 * 根据 文件名如354545.jpg生成新地址
	 * 
	 * @param file
	 * @return
	 */
	public String generateNewPath(String fileName, String folder) {
		fileName = fileName.trim();
		Date date = new Date();
		String fileType = FileUtils.getFileType(fileName);
		if (fileType.equals("picture")) {
			try {
				// spec = "_" + ImageUtils.getSpec(file.getInputStream()); //
				// 宽x高
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String f = "";
		if (!StringUtils.isEmpty(folder)) {
			f = SysConstants.FILE_PATH + File.separator + folder + File.separator
					+ DateUtils.dateToStringFormat(date, "yyyyMMdd");
		} else {
			f = SysConstants.FILE_PATH  + File.separator
					+ DateUtils.dateToStringFormat(date, "yyyyMMdd");
		}

		String filePath = new StringBuilder()
			.append(f).append(File.separator)
			.append(DateUtils.dateToStringFormat(date, "yyyyMMddHHmmss")+ (Math.random() + "").substring(2, 8))
			.append(FileUtils.getExtension(fileName)).toString().replaceAll("\\\\", "/");
		
		return filePath;
	}
}

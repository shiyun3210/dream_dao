package com.fm.file.web.controller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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
public class ImageController {
	
	/**
	 * 图片 缩放比例  宽高 区间
	 */
	private static final int IMG_SCALE_WH_INTERVAL = 50;
	/**
	 * 当前jgp 图片 输出质量，质量越高，图片越大
	 */
	private static final double IMG_QUALITY = 0.9d;
	
	private static final String FLAG = "@";
	
	private static final String COLOR = "#9E9E9E"; 
	
	
	public static void main(String[] args) {
        // 登陆 Url  
        String indexUrl = "http://sz.szchdx.cn/index.htm";  
        // 需登陆后访问的 Url  
        String dataUrl = "http://localhost/applyopen/printApprize.jsp?iid=1516";  
        
        String loginUrl = "http://sz.szchdx.cn/dologin.action";
        
        String listUrl = "http://sz.szchdx.cn/quoteList.action?gsdm=26280&km=&pp=%E5%8D%8E%E4%B8%BA&network=&tykhgsdm=";
        HttpClient httpClient = new HttpClient();  
        
        GetMethod getMethod = new GetMethod(indexUrl);  
        
        try {
			httpClient.executeMethod(getMethod);
			
//			System.out.println(getMethod.getResponseBodyAsString());
			
			GetMethod getMethod1 = new GetMethod("http://sz.szchdx.cn/image.jsp");  
			
			httpClient.executeMethod(getMethod1);
			byte[] image = getMethod1.getResponseBody();
			Thumbnails.of(new ByteArrayInputStream(image)).size(60, 30).toFile(new File("f://asdf.jpg"));
			Scanner xx = new Scanner( System.in );
			String sign = xx.next();
			xx.close();
			PostMethod postMethod = new PostMethod(loginUrl);
			postMethod.setParameter("pwd", "123456");
			postMethod.setParameter("dhhm", "13924416571");
			postMethod.setParameter("sign", sign);
			postMethod.setParameter("jiqima", "");
			
			httpClient.executeMethod(postMethod);
			
			System.out.println(postMethod.getResponseBodyAsString());
			
			GetMethod postMethod1 = new GetMethod(listUrl);
			
			httpClient.executeMethod(postMethod1);
			
			String text = postMethod1.getResponseBodyAsString();
			
			text = text.substring(text.indexOf("<table"),text.indexOf("</table>")+8);
			System.out.println(text);
			
			String res = new String(text);
	        
			
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
  
//        // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式  
//        PostMethod postMethod = new PostMethod(loginUrl);  
//  
//        // 设置登陆时要求的信息，用户名和密码  
//        NameValuePair[] data = { new NameValuePair("name", "13924416571"),  
//                new NameValuePair("password", "123456") };
//        postMethod.setRequestBody(data);  
//        try {  
//            // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略  
//            httpClient.getParams().setCookiePolicy(  
//                    CookiePolicy.BROWSER_COMPATIBILITY);  
//            httpClient.executeMethod(postMethod);  
//            // 获得登陆后的 Cookie  
//            Cookie[] cookies = httpClient.getState().getCookies();  
//            StringBuffer tmpcookies = new StringBuffer();  
//            for (Cookie c : cookies) {  
//                tmpcookies.append(c.toString() + ";");  
//            }  
//            // 进行登陆后的操作1581,1602,1603,1610,1609,1608,1607,1606,1605,1620,1619,1617,1616,1622,1626,1642,1648,1647,1657  
//            GetMethod getMethod = new GetMethod(dataUrl);  
//            // 每次访问需授权的网址时需带上前面的 cookie 作为通行证  
//            getMethod.setRequestHeader("cookie", tmpcookies.toString());  
//            // 你还可以通过 PostMethod/GetMethod 设置更多的请求后数据  
//            // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外  
//            postMethod.setRequestHeader("Referer", "http://www.cc");  
//            postMethod.setRequestHeader("User-Agent", "www Spot");  
//            httpClient.executeMethod(getMethod);  
//            // 打印出返回数据，检验一下是否成功  
//            String text = getMethod.getResponseBodyAsString();  
//            System.out.println(text);  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
}
	
	
	/**
	 * 表单上传图片 multipart/form-data
	 * @param handletype 1 采购信息图片规格 2 产品信息图片规格 
	 * @param iswatermark 是否加水印：0 否  1 是
	 * @param wmtype 水印类型：1 产品缩率图文字水印  2 营业执照图片水印
	 * @param wmtext 文字水印:文本内容
	 * @param wmurl 文字水印:英文公司地址
	 * @param wmimgtype 1 中文图片水印  2 英文图片水印 ; 如果wmtype=2加营业执照图片水印，此参数区分英文图片水印和中文图片水印。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="image/upload",method=RequestMethod.POST)
	@ResponseBody
	public Object fileUpload(@RequestParam(required=false,defaultValue="1") int handletype,@RequestParam(required=false,defaultValue="0") int iswatermark,
			@RequestParam(required=false,defaultValue="1") int wmtype,@RequestParam(required=false,defaultValue="") String wmtext,@RequestParam(required=false,defaultValue="") String wmurl,
			@RequestParam(required=false,defaultValue="1") int wmimgtype,HttpServletRequest request,HttpServletResponse response){
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
				map.put("msg", "图片文件是空的");
	            return map;
			}
			String fileType = file.getOriginalFilename();
			fileType = fileType.substring(fileType.lastIndexOf(".")+1, fileType.length()).toLowerCase();
			if(!fileType.equals("jpeg")&&!fileType.equals("jpg")&&!fileType.equals("png")&&!fileType.equals("bmp")&&!fileType.equals("gif")) {
				map.put("code", "-1");
				map.put("msg", "不支持的图片格式");
	            return map;
	        }
			if(wmtype==1){
				map.put("code", "0");
				map.put("msg", "处理成功");
				map.put("data", responseResult(file,handletype,iswatermark,wmtext,wmurl));
			}else if(iswatermark==1&&wmtype==2){
				map.put("code", "0");
				map.put("msg", "处理成功");
				map.put("data", handleImgWatermark(file,wmimgtype));
			}else{
				map.put("code", "-1");
				map.put("msg", "无效的传参");
				return map;
			}
			System.out.println("耗时："+(System.currentTimeMillis()-sss));
			return map;
		}
		map.put("code", "-1");
		map.put("msg", "没有接收到任何数据!");
		return map;
	}
	
	
	
	/**
	 * 等比缩放
	 * @param file
	 * @param handletype
	 * @return
	 */
	public static List<Map<String,Object>> responseResult(MultipartFile file,int handletype,int iswatermark,String wmtext,String wmurl){
		List<Map<String,Object>> listResource = new ArrayList<Map<String,Object>>();
		Date date = new Date();
		final String imgShortPath = generateImgShortPath(date);
		final String imgFullPath = SysConstants.IMAGE_STORE_PATH+imgShortPath+generateImgName(date);
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			FileUtils.createDirs(SysConstants.IMAGE_STORE_PATH+imgShortPath, true);
			String[] resultImgUrl = scalingImage(file.getInputStream(), new String[]{imgFullPath,imgFullPath,imgFullPath,imgFullPath},handletype,iswatermark,wmtext,wmurl);
			for(int i=0;i<resultImgUrl.length;i++){
				String str = resultImgUrl[i];
				map.put("img"+(i+1), str.substring(str.indexOf("zkfiles")+8,str.length()).replaceAll("\\\\", "/")+".jpg");
			}
			resultImgUrl = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		listResource.add(map);
		return listResource;
	}
	
	/**
	 * 生成图片存储路径   \yymmdd\
	 * @return
	 */
	public static String generateImgShortPath(Date date){
		return DateTools.formatDateTime(date, "yyyyMMdd")+
				File.separator;
	}
	
	public static String generateImgName(Date date){
		return DateTools.formatDateTime(date, "yyyyMMddHHmmss")+(Math.random()+"").substring(2, 8)+"@_@x@";
	}
	
//	/**
//	 * 生成图片存储路径   \yymmdd\
//	 * @return
//	 */
//	public static String generateResponseFullPath(Date date){
//		return SysConstants.FILE_IMAGE_STORE_PATH+DateTools.formatDateTime(date, "yyyyMMdd")+
//				File.separator+DateTools.formatDateTime(date, "yyyyMMddHHmmss")+(Math.random()+"").substring(2, 8)+"@_@x@";
//	}
	
	/**
	 * 生成temp文件存储路径 
	 * @return
	 */
	public static String generateTempFilePath(){
		return SysConstants.RES_ROOT_PATH+File.separator+"temp"+File.separator;
	}
	
	private static String[] scalingImage(InputStream in,String[] imgFullPath,int handletype,int iswatermark,String wmtext,String wmurl){
		try {
			BufferedImage sourceImg = ImageIO.read(in);
			final int height = sourceImg.getHeight();
	        final int width = sourceImg.getWidth();
	        double spec = 0;
	        for(int i=0;i<4;i++){
	        	switch (i) {
				case 0:
					spec = 750d;
					if((Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||width<spec)&&(Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||height<spec)){
						imgFullPath[i] = imgFullPath[i].replaceFirst(FLAG, i+"").replaceFirst(FLAG, width+"").replaceFirst(FLAG, height+"");
						Thumbnails.of(sourceImg).size(width, height).outputFormat("jpg").outputQuality(IMG_QUALITY).toFile(imgFullPath[i]);
						continue;
					}
					break;
				case 1:
					if(handletype==1){
						continue;
					}
					spec = 400d;
					if((Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||width<spec)&&(Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||height<spec)){
						imgFullPath[i] = imgFullPath[i].replaceFirst(FLAG, i+"").replaceFirst(FLAG, width+"").replaceFirst(FLAG, height+"");
						Thumbnails.of(sourceImg).size(width, height).outputFormat("jpg").outputQuality(IMG_QUALITY).toFile(imgFullPath[i]);
						//规格400x400的产品图，加水印
			        	if(iswatermark==1){
		        			handleTextWatermark(imgFullPath[i], wmtext,wmurl);
			        	}
						continue;
					}
					break;	
				case 2:
					if(handletype==1){
						continue;
					}
					spec = 200d;
					if((Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||width<spec)&&(Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||height<spec)){
						imgFullPath[i] = imgFullPath[i].replaceFirst(FLAG, i+"").replaceFirst(FLAG, width+"").replaceFirst(FLAG, height+"");
						Thumbnails.of(sourceImg).size(width, height).outputFormat("jpg").outputQuality(IMG_QUALITY).toFile(imgFullPath[i]);
						continue;
					}
					break;	
				case 3:
					spec = 80d;
					if((Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||width<spec)&&(Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||height<spec)){
						imgFullPath[i] = imgFullPath[i].replaceFirst(FLAG, i+"").replaceFirst(FLAG, width+"").replaceFirst(FLAG, height+"");
						Thumbnails.of(sourceImg).size(width, height).outputFormat("jpg").outputQuality(IMG_QUALITY).toFile(imgFullPath[i]);
						continue;
					}
					break;	
				default:
					break;
				}
	        	double scaleSize = new BigDecimal(spec/(double)(width>height?width:height)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
	        	imgFullPath[i] = imgFullPath[i].replaceFirst(FLAG, "").replaceFirst(FLAG, ((int)(width*scaleSize))+"").replaceFirst(FLAG, ((int)(height*scaleSize))+"");
	        	Thumbnails.of(sourceImg).scale(scaleSize).outputFormat("jpg").outputQuality(IMG_QUALITY).toFile(imgFullPath[i]);
	        	//规格400x400的产品图，加水印
	        	if(i==1&&handletype==2&&iswatermark==1){
        			handleTextWatermark(imgFullPath[i], wmtext,wmurl);
	        	}
	        }
	        if(handletype==1){
	        	return new String[]{imgFullPath[0],imgFullPath[3]};
	        }
			return imgFullPath;
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
	
	/**
	 * 加文字水印
	 * @param srcImg
	 * @param text
	 */
	public static void handleTextWatermark(String srcImg,String text,String wmurl){
		if(StringUtils.isNotBlank(srcImg)&&StringUtils.isNotBlank(text)){
			try {
	            File file = new File(srcImg.replaceFirst("\\\\", "").replaceFirst("\\\\", "/")+".jpg");  
	            Image image = ImageIO.read(file);  
	            int width = image.getWidth(null);  
	            int height = image.getHeight(null);  
	            Font font = new Font("黑体", Font.BOLD, 18);
	            FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);  
	            int widthText = fm.stringWidth(text);
	            int heightText = font.getSize();
	            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
	            Graphics2D g = bufferedImage.createGraphics();  
	            g.drawImage(image,0,0, width, height, null);
	        	g.setFont(font);  
	            g.setColor(Color.getColor(COLOR, Color.gray));
	            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
	            int x = width/2-widthText/2;
	            int y = height/10*7-heightText/2;
	            g.drawString(text,x,y);//水印位置 居中  Y轴 3/7比例
	            if(StringUtils.isNotBlank(wmurl)){
	            	int enX = fm.stringWidth(wmurl);
		            g.drawString(wmurl,width/2-enX/2, y+20);//水印位置 居中  Y轴 3/7比例
	            }
	            g.dispose();
	            ImageIO.write(bufferedImage, "jpg", file);
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }
		}
	}
	
	/**
	 * 加图片水印
	 * @param file
	 * @return
	 */
	public static List<Map<String,Object>> handleImgWatermark(MultipartFile file,int wmimgtype){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Date date = new Date();
		String imgShortPath = generateImgShortPath(date);
		String imgPath = imgShortPath + generateImgName(date);
		String rootPath = SysConstants.IMAGE_STORE_PATH.replaceFirst("\\\\", "");
		try {
			BufferedImage srcImg = ImageIO.read(file.getInputStream());
			int width = srcImg.getWidth();
			int height = srcImg.getHeight();
			File wmFile = null;
			if(wmimgtype==2){
				wmFile = new File(SysConstants.WATERMARK_IMG_EN_PATH);
			}else{
				wmFile = new File(SysConstants.WATERMARK_IMG_CN_PATH);
			}
			FileUtils.createDirs(rootPath + imgShortPath, true);
			double spec = 1200d;
			//如果图片超出1200x1200规格，缩放图片
			if((Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||width<spec)&&(Math.abs(spec-width)<=IMG_SCALE_WH_INTERVAL||height<spec)){
				imgPath = imgPath.replaceFirst(FLAG, "").replaceFirst(FLAG, width+"").replaceFirst(FLAG, height+"");
				imgPath = imgPath.replaceAll("\\\\", "/");
				String imgFullPath = rootPath+imgPath;
				Thumbnails.of(srcImg).size(width, height).watermark(Positions.CENTER, ImageIO.read(wmFile), 1f).outputFormat("jpg").outputQuality(1.0f).toFile(imgFullPath);
			}else{
				double scaleSize = new BigDecimal(spec/(double)(width>height?width:height)).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
				imgPath = imgPath.replaceFirst(FLAG, "").replaceFirst(FLAG, ((int)(width*scaleSize))+"").replaceFirst(FLAG, ((int)(height*scaleSize))+"");
				imgPath = imgPath.replaceAll("\\\\", "/");
				String imgFullPath = rootPath+imgPath;
				Thumbnails.of(srcImg).scale(scaleSize).watermark(Positions.CENTER, ImageIO.read(wmFile), 1f).outputFormat("jpg").outputQuality(1.0f).toFile(imgFullPath);
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("img1", imgPath+".jpg");
			list.add(map);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list; 
	}
}

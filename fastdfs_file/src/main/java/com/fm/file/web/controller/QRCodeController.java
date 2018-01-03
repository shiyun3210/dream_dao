package com.fm.file.web.controller;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fm.file.util.QRCodeUtils;
import com.fm.file.web.constants.SysConstants;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.GenerateStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;

/**
 * 生成二维码
 * @author syf
 */
@Controller
public class QRCodeController {
	// 默认二维码尺寸
	private static final int QRCODE_DEFAULT_SIZE = 300;
	
	// 推广二维码尺寸
	private static final int QRCODE_PROMOTION_SIZE = 200;
	
	private static final int X_QRCODE = 220;
	private static final int Y_QRCODE = 596;
	private static final float PROMOTION_QRCODE_QUALITY = 0.9f;
	
	@Autowired
	private GenerateStorageClient generateStorageClient;
	@Autowired
	private TrackerClient trackerClient;
	
	
//	@RequestMapping(value="tempqrcode")
//	public @ResponseBody Object tempqrcode(@RequestParam(required=false,defaultValue="") String content,@RequestParam(required=false,defaultValue="") String logopath,
//			HttpServletRequest request,HttpServletResponse response){
//		Map<String,Object> map = new HashMap<String,Object>();
//		InputStream logoIns = null;
//		ByteArrayOutputStream out = null;
//		ByteArrayOutputStream outPromotionQrcode = null;
//		try {
//			out = new ByteArrayOutputStream(); 
//			outPromotionQrcode = new ByteArrayOutputStream();
//			
//			if(StringUtils.isNotBlank(logopath)){
//				logoIns = new ByteArrayInputStream(generateStorageClient.downloadFile(logopath.substring(0,logopath.indexOf("/")), logopath.substring(logopath.indexOf("/")+1,logopath.length()),
//						new DownloadByteArray()));
//			}else{
//				logoIns = new FileInputStream(new File(SysConstants.QRCODE_DEFAULT_LOGO_IMG));
//			}
//			if (logoIns!=null) {
//				StorePath resultFile = null;
//				
//				QRCodeUtils.generateLogoQRCode(QRCODE_PROMOTION_SIZE,content, logoIns, out);
//				outPromotionQrcode = new ByteArrayOutputStream();
//				newPromotionQrcode(new ByteArrayInputStream(out.toByteArray()), outPromotionQrcode);
//				resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(),new ByteArrayInputStream(outPromotionQrcode.toByteArray()) 
//				, outPromotionQrcode.size(), "jpg");
//				
//				if(resultFile!=null){
//					map.put("code", "0");
//					map.put("msg", "处理成功");
//					map.put("data", resultFile.getFullPath());
//					return map;
//				}
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			try {
//				if(outPromotionQrcode!=null){
//					outPromotionQrcode.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		map.put("code", "-1");
//		map.put("msg", "处理失败");
//		return map;
//	}
	
//	@RequestMapping(value="tempqrcode")
//	public @ResponseBody Object tempqrcode(HttpServletRequest request,HttpServletResponse response){
//		Map<String,Object> map = new HashMap<String,Object>();
//		ByteArrayOutputStream out = null;
//		ByteArrayOutputStream outPromotionQrcode = null;
//		try {
//			out = new ByteArrayOutputStream();
//			outPromotionQrcode = new ByteArrayOutputStream();
//			Thumbnails.of(request.getInputStream()).size(200,200).outputFormat("jpg").outputQuality(0.9).toOutputStream(out);
//			newPromotionQrcode(new ByteArrayInputStream(out.toByteArray()), outPromotionQrcode);
//			StorePath resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(),new ByteArrayInputStream(outPromotionQrcode.toByteArray()) 
//			, outPromotionQrcode.size(), "jpg");
//			if(resultFile!=null){
//				map.put("code", "0");
//				map.put("msg", "处理成功");
//				map.put("data", resultFile.getFullPath());
//				return map;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			try {
//				if(outPromotionQrcode!=null){
//					outPromotionQrcode.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		map.put("code", "-1");
//		map.put("msg", "处理失败");
//		return map;
//	}
	
	/**
	 * 生成二维码
	 * @param content
	 * @param logopath
	 * @param type 1 推广二维码  0 其它
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="generateqrcode")
	@ResponseBody
	public Object fileUpload(@RequestParam(required=false,defaultValue="") String content,@RequestParam(required=false,defaultValue="") String logopath,
			@RequestParam(required=false,defaultValue="0")int type,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		InputStream logoIns= null;
		ByteArrayOutputStream out = null;
		ByteArrayOutputStream outPromotionQrcode = null;
		
		try {
			if(StringUtils.isNotBlank(content)){
				if(StringUtils.isNotBlank(logopath)){
					logoIns = new ByteArrayInputStream(generateStorageClient.downloadFile(logopath.substring(0,logopath.indexOf("/")), logopath.substring(logopath.indexOf("/")+1,logopath.length()),
							new DownloadByteArray()));
				}else{
					logoIns = new FileInputStream(new File(SysConstants.QRCODE_DEFAULT_LOGO_IMG));
				}
				if (logoIns!=null) {
					StorePath resultFile = null;
					out = new ByteArrayOutputStream();
					if(type==1){//插入推广二维码图
						QRCodeUtils.generateLogoQRCode(QRCODE_PROMOTION_SIZE,content, logoIns, out);
						outPromotionQrcode = new ByteArrayOutputStream();
						newPromotionQrcode(new ByteArrayInputStream(out.toByteArray()), outPromotionQrcode);
						resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(),new ByteArrayInputStream(outPromotionQrcode.toByteArray()) 
						, outPromotionQrcode.size(), "jpg");
					}else{
						QRCodeUtils.generateLogoQRCode(QRCODE_DEFAULT_SIZE,content, logoIns, out);
						resultFile = generateStorageClient.uploadFile(trackerClient.getStoreStorage().getGroupName(),new ByteArrayInputStream(out.toByteArray()) 
						, out.size(), "jpg");
					}
					if(resultFile!=null){
						map.put("code", "0");
						map.put("msg", "处理成功");
						map.put("data", resultFile.getFullPath());
						return map;
					}
				}
			}else{
				map.put("code", "-1");
				map.put("msg", "无效的传参");
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(logoIns!=null){
					logoIns.close();
				}
				if(out!=null){
					out.close();
				}
				if(outPromotionQrcode!=null){
					outPromotionQrcode.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		map.put("code", "-1");
		map.put("msg", "处理失败");
		return map;
	}
	
	
	
	/**
	 * 插入二维码，到指定图片
	 * @param qrcodeIs 二维码
	 * @param outputStream
	 */
	private static void newPromotionQrcode(InputStream qrcodeIs,OutputStream outputStream) {
		ImageOutputStream outputStreamImg = null;
		try {
			BufferedImage tagImage = ImageIO.read(new File(SysConstants.PROMOTION_QRCODE));
			Image src = ImageIO.read(qrcodeIs);
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			
			Graphics2D graph = tagImage.createGraphics();
			graph.drawImage(src, X_QRCODE, Y_QRCODE, width, height, null);
			Shape shape = new RoundRectangle2D.Float(X_QRCODE, Y_QRCODE, width, width, 0, 0);//直角
			graph.draw(shape);
			graph.dispose();
			//使用ImageWriter设置图片质量
			Iterator<ImageWriter> iter = ImageIO.getImageWriters(ImageTypeSpecifier.createFromRenderedImage(tagImage), "jpg");
			ImageWriter iw = null;
			if(iter.hasNext()) {
				iw = (ImageWriter) iter.next();
			}
			IIOImage iioImage = new IIOImage(tagImage, null, null);
			ImageWriteParam param = iw.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(PROMOTION_QRCODE_QUALITY);
			outputStreamImg = ImageIO.createImageOutputStream(outputStream);
			iw.setOutput(outputStreamImg);
			iw.write(null, iioImage, param);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(outputStreamImg!=null){
					outputStreamImg.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
//		try {
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			newPromotionQrcode(null,outputStream);
//			Thumbnails.of(new ByteArrayInputStream(outputStream.toByteArray())).scale(1.0).outputQuality(0.9d).toFile("F://cc3.jpg");;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}

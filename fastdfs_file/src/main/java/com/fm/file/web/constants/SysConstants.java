package com.fm.file.web.constants;

import java.io.File;

import com.fm.file.util.Config;



/**
 * 
 * @author syf 
 *
 */
public class SysConstants {

	// ---------------------------------------系统配置-------------------------------------------

	//根目录
	public static final String RES_ROOT_PATH = Config.getString("root.resource.dir");
	
	//服务器地址
	public static final String RESURCE_SERVER_URL = Config.getString("server.host");
	
	
	//图片获取
	public static final String IMG_RESOURCE_LOAD = RESURCE_SERVER_URL+"?path=";
	
	public static final String FILE_LOAD_URL = Config.getString("");
	
	//IP白名单	
	public static final String[] IP_WRITES = Config.getString("ip.write").split("#");
	
	/**
	 * 页面缓存 时间
	 */
	public static final int RESURCE_EXPIRE =432000;
	
	
	public static final String FILE_PATH="resources";
	
	public static final String IMAGE_STORE_PATH = RES_ROOT_PATH+File.separator;
	
	public static final String AUDIO_STORE_PATH = RES_ROOT_PATH +File.separator+"audio"+File.separator;
	
	public static final String FILE_TEMP_PATH = RES_ROOT_PATH +File.separator+"temp"+File.separator;
	
	/**
	 * 英文水印图片路径
	 */
	public static final String WATERMARK_IMG_EN_PATH = RES_ROOT_PATH+File.separator+FILE_PATH+File.separator+"watermarken.png";
	
	
	
	/***************************************/
	
	public static final String SYS_FILE_PATH = Config.getString("SYS_FILE_PATH");
	/**
	 * 中文水印图片路径
	 */
	public static final String WATERMARK_IMG_CN_PATH = SYS_FILE_PATH+"watermarkcn.png";
	/**
	 * 二维码存储路径
	 */
//	public static final String QR_CODE_PATH = RES_ROOT_PATH +File.separator + "qrcode" + File.separator;
	
	public static final String QRCODE_DEFAULT_LOGO_IMG = SYS_FILE_PATH+"defaultlqrcodelogo.jpg";
	
	/**
	 * 新的推广二维码
	 */
	public static final String PROMOTION_QRCODE = SYS_FILE_PATH+"promotionqrcode.png";
	
	
}

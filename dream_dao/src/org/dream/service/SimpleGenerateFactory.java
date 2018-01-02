/**
 * 
 */
package org.dream.service;

import java.io.File;


/**
 * @author houzhijia
 * @2014-4-10
 * @下午9:15:50
 */
public class SimpleGenerateFactory {
	
	public static Generatable createInstance(String type, String ftl, String outputPath, String packagePath){
		
		File dir = new File(outputPath+packagePath + "/" + type);
		System.out.println(type + "文件生成路径: [" + dir.getAbsolutePath() + "]");
		if (!dir.exists()){
			dir.mkdirs();
		}
		
		if(type.equals("dao")){
			
			return new DaoGenerater(ftl, outputPath, packagePath);
		}else if(type.equals("iface")){
			
			return new IFaceGenerater(ftl, outputPath, packagePath);
		}else if(type.equals("impl")){
			
			return new ImplGenerater(ftl, outputPath, packagePath);
		}else if(type.equals("client")){
			
			return new ClientGenerater(ftl, outputPath, packagePath);
		}else if(type.equals("bean")){
			
			return new BeanGenerater(ftl, outputPath, packagePath);
		}else if(type.equals("controller")){
			
			return new ControllerGenerater(ftl, outputPath, packagePath);
		}
		
		return null;
	}
}

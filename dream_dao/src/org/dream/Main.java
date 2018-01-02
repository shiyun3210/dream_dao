/**
 * 
 */
package org.dream;

import java.util.Calendar;
import java.util.Date;

import org.dream.service.GenerateManager;
import org.dream.util.CommonConfig;
import org.lemur.service.DaoGenerateManager;


/**
 * @author houzhijia
 * @2014-4-11
 * @下午6:23:31
 */
public class Main {
	
	public static void main(String[] args) {
		
		long time1 = Calendar.getInstance().getTimeInMillis();
		System.out.println("开始生成:" + new Date().toString());
		String daoType = CommonConfig.getPropertyValue("file.dao.type");
		if(daoType.equals("new")){
			dream();
		}else {
			lemur();
		}
		
		
		long time2 = Calendar.getInstance().getTimeInMillis();
		System.out.println("生成完毕, 总共耗时:" + (time2 - time1) + "MS");
	}
	
	
	private static void dream(){
		
		GenerateManager manager = new GenerateManager();
		manager.execute();
	}
	
	private static void lemur(){
		
		DaoGenerateManager manager = new DaoGenerateManager();
		manager.generate();
	}
}

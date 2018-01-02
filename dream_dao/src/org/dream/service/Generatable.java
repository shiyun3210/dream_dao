/**
 * 
 */
package org.dream.service;

import java.util.List;


/**
 * 生成器接口
 * @author houzhijia
 * @2014-4-10
 * @下午9:00:13
 */
public interface Generatable {
	
	String getTemplateFileName();
	
	String getOutputPath();
	
	String getPackage();
	
	void generate(String tableName, List<ColumnBean> columnList);
}

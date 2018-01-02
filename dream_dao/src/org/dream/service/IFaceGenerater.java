package org.dream.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dream.util.FreemarkerUtils;
import org.dream.util.StringUtils;

/**
 * IFace生成器
 * 
 * @author houzhijia
 * @2014-4-10
 * @下午9:01:34
 */
public class IFaceGenerater implements Generatable {

	private static String TYPE = "iface";
	private final String ftl;
	private final String outputPath;
	private final String packagePath;

	@Override
	public String getTemplateFileName(){

		return ftl;

	}

	public IFaceGenerater(String ftl, String outputPath, String packagePath){

		this.ftl = ftl;
		this.outputPath = outputPath;
		this.packagePath = packagePath;
	}

	@Override
	public String getOutputPath(){

		return outputPath;
	}

	@Override
	public String getPackage(){

		return packagePath;
	}

	@Override
	public void generate(String tableName, List<ColumnBean> columnList){

		if (!hasPrimary(columnList)){
			System.out.println("表[" + tableName + "]没有设置主键, 忽略生成!" + TYPE);
			return;
		}

		Map<String, Object> root = new HashMap<String, Object>();

		String packages = packagePath.replace("/", ".");
		root.put("packages", packages + "." + TYPE);
		root.put("basePackages", packages);
		root.put("tableName", tableName);

		String className = StringUtils.transformTableNameToSpecialName(tableName, "IFace");
		root.put("className", className);
		String beanClassName = StringUtils.transformTableNameToSpecialName(tableName, "Bean");
		root.put("beanClassName", beanClassName);
		String beanVarName = StringUtils.transformTableNameToSpecialName(tableName, "bean");
		root.put("beanVarName", beanVarName);
		String daoClassName = StringUtils.transformTableNameToDaoName(tableName);
		root.put("daoClassName", daoClassName);
		String daoVarName = StringUtils.transformTableNameToDaoVarName(tableName);
		root.put("daoVarName", daoVarName);

		String outputFile = outputPath + packagePath + "/" + TYPE + "/" + className + ".java";
		FreemarkerUtils.process(root, ftl, getClass(), outputFile);
		System.out.println("表[" + tableName + "]生成" + TYPE + "完毕!");
	}

	private boolean hasPrimary(List<ColumnBean> columnList){

		for (ColumnBean column : columnList){
			if (column.isPrimary())
				return true;
		}

		return false;
	}
}
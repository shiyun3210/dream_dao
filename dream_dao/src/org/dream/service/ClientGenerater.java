package org.dream.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dream.util.FreemarkerUtils;
import org.dream.util.StringUtils;

public class ClientGenerater implements Generatable {

	private static String TYPE = "client";
	private final String ftl;
	private final String outputPath;
	private final String packagePath;

	@Override
	public String getTemplateFileName(){

		return ftl;

	}

	public ClientGenerater(String ftl, String outputPath, String packagePath){

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

		String className = StringUtils.transformTableNameToSpecialName(tableName, "Client");
		root.put("className", className);
		String implClassName = StringUtils.transformTableNameToSpecialName(tableName, "IFaceImpl");
		root.put("implClassName", implClassName);
		String ifacClassName = StringUtils.transformTableNameToSpecialName(tableName, "IFace");
		root.put("ifacClassName", ifacClassName);
		String ifacVarName = StringUtils.transformTableNameToVarName(tableName, "IFace");
		root.put("ifacVarName", ifacVarName);
		String beanClassName = StringUtils.transformTableNameToSpecialName(tableName, "Bean");
		root.put("beanClassName", beanClassName);
		String beanVarName = StringUtils.transformTableNameToVarName(tableName, "Bean");
		root.put("beanVarName", beanVarName);

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
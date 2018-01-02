package org.dream.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dream.util.FreemarkerUtils;
import org.dream.util.StringUtils;

public class DaoGenerater implements Generatable {

	private static String TYPE = "dao";
	private final String ftl;
	private final String outputPath;
	private final String packagePath;

	@Override
	public String getTemplateFileName(){

		return ftl;

	}

	public DaoGenerater(String ftl, String outputPath, String packagePath){

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
			System.out.println("表[" + tableName + "]没有设置主键, 忽略生成!"
					+ TYPE);
			return;
		}

		StringUtils.toUpperFirstWord(TYPE);

		Map<String, Object> root = new HashMap<String, Object>();
		String packages = packagePath.replace("/", ".");
		root.put("packages", packages + "." + TYPE);
		root.put("basePackages", packages);
		root.put("tableName", tableName);
		root.put("table", columnList);

		String className = StringUtils
				.transformTableNameToDaoName(tableName);
		root.put("className", className);
		root.put("classBeanName", className + "Bean");

		List<ColumnBean> prilist = new ArrayList<ColumnBean>();
		List<ColumnBean> normallist = new ArrayList<ColumnBean>();
		for (ColumnBean column : columnList){
			if (column.isPrimary())
				prilist.add(column);
			else
				normallist.add(column);
		}
		root.put("prilist", prilist);
		root.put("normallist", normallist);

		String outputFile = outputPath + packagePath + "/" + TYPE + "/"
				+ className + "DAO.java";
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
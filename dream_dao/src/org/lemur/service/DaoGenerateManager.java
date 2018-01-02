package org.lemur.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dream.util.CommonConfig;
import org.dream.util.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class DaoGenerateManager
{

	@SuppressWarnings("unchecked")
	public void generate()
	{
		String driver = CommonConfig.getPropertyValue("database.mysql.driver");
		String url = CommonConfig.getPropertyValue("database.mysql.url");
		String username = CommonConfig.getPropertyValue("database.mysql.username");
		String password = CommonConfig.getPropertyValue("database.mysql.password");
		System.out.println("database:" + url);
		String tableName = CommonConfig.getPropertyValue("database.mysql.table");
		int ftlfile = Integer.parseInt(CommonConfig.getPropertyValue("ftl.file.config"));
		String tempFileName = "bean.ftl";
		switch (ftlfile) {
		case 1:
			tempFileName = "dao_mysql.ftl";
			System.out.println("使用DAO_MYSQL配置策略");
			break;
		default:
			System.out.println("使用BEAN_SPRING配置策略");
		}

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(driver);
			System.out.println( url +" -- "+username+" -- "+password);
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();

			List <String>tables = new ArrayList<String>();

			DatabaseMetaData databaseMetaData = conn.getMetaData();
			if ((tableName == null) || (tableName.trim().length() == 0)) {
				ResultSet tableSet = databaseMetaData.getTables(null, "%", "%", new String[] { "TABLE" });
				while (tableSet.next()){
					tables.add(tableSet.getString("TABLE_NAME").toLowerCase());
				}
			}
			else {
				for (String tableN : tableName.split(",")) {
					if (tableN.trim().length() > 0) {
						tables.add(tableN.trim());
					}
				}
			}
			String daoPath = CommonConfig.getPropertyValue("file.out.path");
			String fileDir = CommonConfig.getPropertyValue("classdir") + "/out/" + daoPath.replace(".", "/");
			File dir = new File(fileDir);
			System.out.println("文件生成路径: [" + dir.getAbsolutePath() + "]");
			if (!dir.exists())
				dir.mkdirs();


			for (String tname : tables) {
				List columnList = new ArrayList();

				ResultSet columnSet = databaseMetaData.getColumns(null, "%", tname, "%");

				ResultSet tableDesc = stmt.executeQuery("desc " + tname);

				while (columnSet.next()) {
					ColumnBean column = new ColumnBean();
					String columname =  columnSet.getString("COLUMN_NAME").toLowerCase();
					String columnName = StringUtils.toUpperFirstWord( columname);
					column.setColumnName(columnName);
					String type = columnSet.getString("TYPE_NAME").toLowerCase();
					column.setType(type);
					String remarks = columnSet.getString("REMARKS"); 
					column.setDescription(remarks);
					column.setAllowNull(columnSet.getBoolean("IS_NULLABLE"));

					tableDesc.next();

					if ("PRI".equals(tableDesc.getString(4))) {
						column.setPrimary(true);
					}
					if ("auto_increment".equalsIgnoreCase(tableDesc.getString(6))) {
						column.setAutoId(true);
					}
					column.setAllowNull(tableDesc.getBoolean(3));

					columnList.add(column);
				}

				generateFile(daoPath, tempFileName, tname, columnList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try
			{
				stmt.close();
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		finally
		{
			try
			{
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void generateFile(String daoPath, String ftl, String tableName, List<ColumnBean> table) throws Exception {
		try {
			Configuration cfg = new Configuration();
			cfg.setClassForTemplateLoading(getClass(), "temp");
			cfg.setObjectWrapper(new DefaultObjectWrapper());

			Template temp = cfg.getTemplate(ftl);
			Map root = new HashMap();

			root.put("daoPath", daoPath);
			String fileDir = CommonConfig.getPropertyValue("classdir") + "/out/" + daoPath.replace(".", "/");

			String fileName = StringUtils.toUpperFirstWord(tableName);
			root.put("tableName", tableName);
			root.put("className", fileName);
			root.put("table", table);
			List prilist = new ArrayList();
			List normallist = new ArrayList();
			for (ColumnBean column : table) {
				if (column.isPrimary())
					prilist.add(column);
				else
					normallist.add(column);
			}
			if (!prilist.isEmpty()) {
				root.put("prilist", prilist);
			} else {
				System.out.println("表[" + tableName + "]没有设置主键, 忽略生成!");
				return;
			}
			if (!normallist.isEmpty())
				root.put("normallist", normallist);
			File file = new File(fileDir + "/" + fileName + ".java");

			Object fileout = new OutputStreamWriter(new FileOutputStream(file));
			temp.setOutputEncoding("GBK");
			temp.process(root, (Writer)fileout);
			
			((Writer)fileout).flush();
			System.out.println("表[" + tableName + "]生成完毕!");
		} catch (Exception e) {
			System.out.println("表[" + tableName + "]配置有误, 忽略生成!");
			e.printStackTrace();
		}
	}

}
package org.dream.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.dream.bean.ConnectionManager;
import org.dream.util.CommonConfig;
import org.dream.util.StringUtils;

public class GenerateManager
{
	private final List<Generatable> generateList =  new ArrayList<Generatable>();
	private ConnectionManager connectionManager;
	
	private final List <String> tableList = new ArrayList<String>();

	private void resolveGenerater(){
		
		String outputPath = CommonConfig.getPropertyValue("file.outputpath");
		if(outputPath == null || outputPath.trim().isEmpty()){
			outputPath =  CommonConfig.getPropertyValue("classdir") + "/out/";
		}else{
			outputPath = outputPath + "/";
		}
		
		String packagePath = CommonConfig.getPropertyValue("file.package");
		if(packagePath != null){
			packagePath = packagePath.trim().replace(".", "/");
		}
		
		boolean generateDao = CommonConfig.getBooleanValue("file.generate.dao");
		if(generateDao){
			generateList.add(SimpleGenerateFactory.createInstance("dao", "dao.ftl", outputPath, packagePath));
			System.out.println("生成 dao");
		}
		
		boolean generateIface = CommonConfig.getBooleanValue("file.generate.iface");
		if(generateIface){
			generateList.add(SimpleGenerateFactory.createInstance("iface", "iface.ftl", outputPath, packagePath));
			System.out.println("生成 iface");
		}
		
		boolean generateImpl = CommonConfig.getBooleanValue("file.generate.impl");
		if(generateImpl){
			generateList.add(SimpleGenerateFactory.createInstance("impl", "impl.ftl", outputPath, packagePath));
			System.out.println("生成 impl");
		}
		
		boolean generateClient = CommonConfig.getBooleanValue("file.generate.client");
		if(generateClient){
			generateList.add(SimpleGenerateFactory.createInstance("client", "client.ftl", outputPath, packagePath));
			System.out.println("生成 client");
		}
		
		boolean generateBean = CommonConfig.getBooleanValue("file.generate.bean");
		if(generateBean){
			generateList.add(SimpleGenerateFactory.createInstance("bean", "bean.ftl", outputPath, packagePath));
			System.out.println("生成 bean");
		}
		
		
		boolean generateController = CommonConfig.getBooleanValue("file.generate.controller");
		if(generateController){
			generateList.add(SimpleGenerateFactory.createInstance("controller", "controller.ftl", outputPath, packagePath));
			System.out.println("生成 controller");
		}
		
	}
	
	private void resolveDriver(){
		
		String driver = CommonConfig.getPropertyValue("database.mysql.driver");
		String url = CommonConfig.getPropertyValue("database.mysql.url");
		String username = CommonConfig.getPropertyValue("database.mysql.username");
		String password = CommonConfig.getPropertyValue("database.mysql.password");
		
		connectionManager = new ConnectionManager(driver, url, username, password);
		
	}
	
	private void resolveTables(Connection conn) throws SQLException{
		
		DatabaseMetaData databaseMetaData = conn.getMetaData();
		String tableNames = CommonConfig.getPropertyValue("database.mysql.tables");
		
		if ((tableNames == null) || (tableNames.trim().length() == 0)) {
			ResultSet tableSet = databaseMetaData.getTables(null, "%", "%", new String[] { "TABLE" });
			while (tableSet.next()){
				tableList.add(tableSet.getString("TABLE_NAME").toLowerCase());
			}
		}else {
			for (String tableN : tableNames.split(",")) {
				if (tableN.trim().length() > 0) {
					tableList.add(tableN.trim());
				}
			}
		}
	}
	
	public void execute(){
		
		resolveGenerater();
		resolveDriver();
		Connection conn = null;
		try {
			conn = connectionManager.getConnection();
			resolveTables(conn);
			generate(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			connectionManager.returnConnection(conn);
		}
		
	}
	
	
	private void addToColumnList(List<ColumnBean> columnList, ResultSet columnSet, ResultSet tableDesc) throws SQLException{
		
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
	}
	
	private void generate(Connection conn)
	{
		
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			DatabaseMetaData databaseMetaData = conn.getMetaData();
			for (String tname : tableList) {
				List<ColumnBean> columnList = new ArrayList<ColumnBean>();

				ResultSet columnSet = databaseMetaData.getColumns(null, "%", tname, "%");

				ResultSet tableDesc = stmt.executeQuery("desc " + tname);

				addToColumnList(columnList, columnSet, tableDesc);
				
				for(Generatable generatable:generateList){
					generatable.generate(tname, columnList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
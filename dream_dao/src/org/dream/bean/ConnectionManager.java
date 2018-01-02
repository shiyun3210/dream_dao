/**
 * 
 */
package org.dream.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



/**
 * @author houzhijia
 * @2014-4-10
 * @下午9:21:53
 */
public class ConnectionManager {
	
	private final String driver;
	private final String url;
	private final String username;
	private final String password;
	
	public ConnectionManager(String driver,String url,String username,String password){
		
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}
	public String getDriver() {
	
		return driver;
	}
	
	public String getUrl() {
	
		return url;
	}
	
	public String getUsername() {
	
		return username;
	}
	
	public String getPassword() {
	
		return password;
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		
		Class.forName(driver);
		return DriverManager.getConnection(url, username, password);
		
	}
	
	
	public void returnConnection(Connection conn){
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

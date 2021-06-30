package org.violetime.autopers.database;

import java.sql.Connection;
/**
 * 数据库连接
 * @author taoyo
 *
 */
public class AutopersConnection {
	private Connection connection;
	public AutopersConnection(Connection connection){
		this.connection=connection;
	}
	/**
	 * 获取数据库连接
	 * @return
	 */
	public Connection getConnection() {
		return connection;
	}
	private String session;
	/**
	 * 数据会话
	 * @return
	 */
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}

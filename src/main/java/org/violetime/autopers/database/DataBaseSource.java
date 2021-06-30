package org.violetime.autopers.database;

import java.util.List;
public class DataBaseSource {
	public List<DataBaseTable> getTables() {
		return tables;
	}
	public void setTables(List<DataBaseTable> tables) {
		this.tables = tables;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private List<DataBaseTable> tables;
	private String name;
	private String defaultSource;
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private String initialSize = "2";
	private String sessionMaxLifeTime = "10000";
	private String sessionPoolCapacity = "10";
	private String sessionPoolMinSize = "1";
	public String getDefaultSource() {
		return defaultSource;
	}
	public void setDefaultSource(String defaultSource) {
		this.defaultSource = defaultSource;
	}
	public String getDriverClassName() {
		return driverClassName;
	}
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getInitialSize() {
		return Integer.parseInt(initialSize);
	}
	public Long getSessionMaxLifeTime() {
		return Long.parseLong(sessionMaxLifeTime);
	}
	public Integer getSessionPoolCapacity() {
		return Integer.parseInt(sessionPoolCapacity);
	}
	public Integer getSessionPoolMinSize() {
		return Integer.parseInt(sessionPoolMinSize);
	}
	public void setInitialSize(String initialSize) {
		this.initialSize = initialSize;
	}
	public void setSessionMaxLifeTime(String sessionMaxLifeTime) {
		this.sessionMaxLifeTime = sessionMaxLifeTime;
	}
	public void setSessionPoolCapacity(String sessionPoolCapacity) {
		this.sessionPoolCapacity = sessionPoolCapacity;
	}
	public void setSessionPoolMinSize(String sessionPoolMinSize) {
		this.sessionPoolMinSize = sessionPoolMinSize;
	}
}

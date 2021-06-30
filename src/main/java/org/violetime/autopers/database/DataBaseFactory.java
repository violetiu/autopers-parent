package org.violetime.autopers.database;

import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionFactory;
import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据库基础配件工厂
 * 
 * @author taoyo
 *
 */
public class DataBaseFactory {
	private final static Logger logger=Logger.getLogger("Autopers");
	/**
	 * 获取数据库配置资源
	 * 
	 * @param mappingClass
	 * @return
	 */
	public static DataBaseSource getDataBaseSource(AutopersMappingClass mappingClass) {

		String source= mappingClass.getSource();
		if(source!=null&&source.length()>0){
			DataBaseSource baseSource=dataBaseSourceMap.get(source);
			if(baseSource!=null)
				return  baseSource;
		}

		// TODO
		if (dataBaseSource != null && dataBaseSource.getTables() != null)
			for (DataBaseTable baseTable : dataBaseSource.getTables()) {
				if (baseTable.getTableName().equals(mappingClass.getName())) {
					return dataBaseSource;
				}
			}
		for (DataBaseSource baseSource : dataBaseSourceMap.values()) {
			if (baseSource == dataBaseSource)
				continue;
			// TODO
			if (baseSource != null && baseSource.getTables() != null)
				for (DataBaseTable baseTable : baseSource.getTables()) {
					if (baseTable.getTableName().equals(mappingClass.getName())) {
						return baseSource;
					}
				}
		}
		logger.info(mappingClass.getName()+ ",Not found DataBaseSource!");
		return null;
	}

	/**
	 * 获取数据库配置资源
	 * 
	 * @return
	 */
	public static DataBaseSource getDataBaseSource() {
		if (dataBaseSource != null)
			return dataBaseSource;
		return null;
	}

	/**
	 * 获取数据库连接
	 * 
	 * @param baseSource 数据库配置资源
	 * @return
	 */
	public static Connection getConnection(DataBaseSource baseSource) {
		try {
			Class.forName(baseSource.getDriverClassName());
			Connection connection = DriverManager.getConnection(baseSource.getUrl(), baseSource.getUsername(),
					baseSource.getPassword());
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取数据库配置资源
	 * 
	 * @param baseSourceName 数据库配置资源
	 * @return
	 */
	public static DataBaseSource getDataBaseSource(String baseSourceName) {
		if (dataBaseSourceMap != null) {
			DataBaseSource baseSource = dataBaseSourceMap.get(baseSourceName);
			if (baseSource != null)
				return baseSource;
		}
		return null;
	}
	private static DataBaseSource dataBaseSource;
	private static HashMap<String, DataBaseSource> dataBaseSourceMap;

	/**
	 * 增加数据连接配置资源
	 * 
	 * @param baseSource
	 */
	public static void addDataBaseSource(DataBaseSource baseSource) {
		if (dataBaseSourceMap == null)
			dataBaseSourceMap = new HashMap<>();
		dataBaseSourceMap.put(baseSource.getName(), baseSource);
		if (baseSource.getDefaultSource() != null&&baseSource.getDefaultSource().toLowerCase().equals("true")) {
			dataBaseSource = baseSource;
		}
	}
	public static HashMap<String, DataBaseSource> getDataBaseSourceMap() {
		return dataBaseSourceMap;
	}

	/**
	 * 是否存在表
	 * @param baseSource
	 * @param table
	 * @return
	 */
	public static boolean hasTable(DataBaseSource baseSource ,String table){
		if(baseSource==null||table==null)
			return  false;
		List<DataBaseTable> baseTables = baseSource.getTables();
		if(baseTables==null)
			return  false;
		for (DataBaseTable baseTable : baseTables) {
			if(baseTable.getTableName().equals(table))
				return true;
		}
		return  false;
	}
	public static  List<String> getTableParts(DataBaseSource baseSource ,String table){
		if(baseSource==null||table==null)
			return  null;
		List<DataBaseTable> baseTables = baseSource.getTables();
		if(baseTables==null)
			return  null;
		List<String> results=new ArrayList<>();
		for (DataBaseTable baseTable : baseTables) {
			if(baseTable.getTableName().startsWith(table+"$"))
				results.add(baseTable.getTableName());
		}
		return results;
	}
	public static  void putDataBaseSourceTable(DataBaseSource baseSource,String tableName){
		for (DataBaseSource baseSource1 : dataBaseSourceMap.values()) {
			if(baseSource1.getName().equals(baseSource.getName())){
				DataBaseTable dataBaseTable=new DataBaseTable();
				dataBaseTable.setTableName(tableName);
				baseSource1.getTables().add(dataBaseTable);
			}
		}
	}

	/**
	 * 初始化资源
	 */
	public static void initDataBaseSourceTables() {
		for (DataBaseSource baseSource : dataBaseSourceMap.values()) {
			AutopersSession persSession = AutopersSessionFactory.openSession(baseSource);
			List<DataBaseTable> baseTables = persSession.getTables();
			persSession.close();
			baseSource.setTables(baseTables);
		}
	}
}

package org.violetime.autopers.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.List;

import org.violetime.autopers.database.DataBaseColumn;
import org.violetime.autopers.database.DataBaseTable;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.query.AutopersQuery;

/**
 * <p>数据库会话接口
 * <p>提供对数据库的操作，包括增删改查等
 */
public interface AutopersSession {
	/**
	 * 复制一个会话
	 * @return
	 */
	public AutopersSession copySession();
	public long getIndex();
	/**
	 * 获取回话已存在时间
	 * @return
	 */
	public Long lifeTime();
	/**
	 * 设置回话已存在的时间
	 * @param time
	 */
	public void lifeTime(Long time);
	/**
	 * 获取回话已存在时间
	 * @return
	 */
	public Long useTime();
	/**
	 * 设置回话已存在的时间
	 * @param time
	 */
	public void useTime(Long time);
	/**
	 * 数据库连接
	 *
	 */
	public Connection getConnection();
	/**
	 * 开始执行事务
	 * @return
	 * @throws SQLException
	 */
	public AutopersTransaction beginTransaction() throws SQLException;
	/**
	 * 关闭会话
	 */
	public void close();

	public PreparedStatement getPreparedStatement() throws SQLException;
	public PreparedStatement getPreparedStatement(String sql) throws SQLException;
	public PreparedStatement closePreparedStatement(PreparedStatement preparedStatement);
	/**
	 * 如果存在，则修改。如果不存就新增
	 * @param autoPersObject
	 * @return
	 */
	public AutopersObject saveObject(AutopersObject autoPersObject) throws SQLException;
	/**
	 * 保存操作
	 * @param autoPersObjects
	 * @return
	 * @throws SQLException
	 */
	public Integer saveList(List<? extends AutopersObject> autoPersObjects)throws SQLException;
	
	/**
	 * 插入操作
	 * @param autoPersObject
	 * @return
	 * @throws SQLException
	 */
	public AutopersObject insertObject(AutopersObject autoPersObject) throws SQLException;
	/**
	 * 插入操作
	 * @param autoPersObjects
	 * @return
	 * @throws SQLException
	 */
	public Long insertList(List<? extends AutopersObject> autoPersObjects) throws SQLException;
	/**
	 * 拷贝数据
	 * @param autoPersObject。Query 不能为空。
	 * @return
	 * @throws SQLException
	 */
	
	public Integer copyData(AutopersObject autoPersObject) throws SQLException;
	
	/**
	 * 修改
	 * @param autoPersObject
	 * @return
	 * @throws SQLException
	 */
	public AutopersObject updateObject(AutopersObject autoPersObject) throws SQLException; 
	/**
	 * 修改
	 * @param autoPersObjects
	 * @return
	 * @throws SQLException
	 */
	public AutopersObject updateList(List<? extends AutopersObject> autoPersObjects) throws SQLException;
	
	/**
	 * 删除
	 * @param autoPersObject
	 * @return
	 * @throws SQLException
	 */
	public Integer deleteObject(AutopersObject autoPersObject) throws SQLException;
	/**
	 * 删除
	 * @param list
	 * @param object
	 * @return
	 * @throws SQLException
	 */
	public Long deleteList(List<? extends AutopersObject> list, AutopersObject object) throws SQLException;
	/**
	 * 删除
	 * @param autoPersQuery
	 * @return
	 * @throws SQLException
	 */
	public Integer deleteQuery(AutopersQuery autoPersQuery)throws SQLException;
	/**
	 * 查询
	 * @param autoPersObject
	 * @return
	 * @throws SQLException
	 */
	public List<AutopersObject> queryObject(AutopersObject autoPersObject)throws SQLException;
	/**
	 * 查询
	 * @param autoPersQuery
	 * @return
	 * @throws SQLException
	 */
	public List<AutopersObject> query(AutopersQuery autoPersQuery)throws SQLException ;

	/**
	 * 查询个数
	 * @param autoPersQuery
	 * @return
	 * @throws SQLException
	 */
	public int queryCount(AutopersQuery autoPersQuery)throws SQLException ;
	
	/**
	 * 查询个数
	 * @param autoPersObject
	 * @return
	 * @throws SQLException
	 */
	public int queryObjectCount(AutopersObject autoPersObject)throws SQLException ;
	
	/**
	 * 执行存储过程
	 * @param procedureName
	 */
	public void storedProcedure(String procedureName,String[] args)throws SQLException;
	/**
	 * 执行存储过程  带返回参数
	 * @param procedureName
	 * @param args
	 * @param outSqlType
	 * @return
	 * @throws SQLException
	 */
	public Object storedProcedureResult(String procedureName, String[] args,SQLType outSqlType)
			throws SQLException;
	
	/**
	 * 新建并初始化，一个数据存储实体类
	 * @param autoPersObject
	 * @return
	 */
	public AutopersObject newAutopersObject(Class<?> autoPersObject);

	/**
	 * 获取数据库会话中所有的表
	 * @return
	 */
	public List<DataBaseTable> getTables();
	/**
	 * 获取表的列集合
	 * @return
	 */
	public List<DataBaseColumn> getColumns(String tableName);
	/**
	 * 按照mappingField从对象中获取sql对应的值
	 * @param mappingField
	 * @param object
	 * @return
	 */
	public String getSqlValueByField(AutopersMappingField mappingField,Object object);

	public String codeId();
	public void codeId(String codeId);

	public void copyTableStructure(String newTable,String table);



}

package org.violetime.autopers.objects;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.objects.combine.CombineDictionaryObject;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.query.AutopersQueryPage;
/**
 * <p>数据库操作接口
 * <ul>
 * <li>所有数据表接口的父级接口
 * <li>用于执行基于接口代理类的数据基本操作，包括增删改查等
 * <li>由AutopersObjectProxy提供方法代理
 * </ul>
 * @author taoyo
 */
public interface AutopersObject {
	/**
	 * 定义返回结果集
	 * @param field
	 * @return
	 */
	public AutopersFunction _Result(Object field);
	/**
	 * 定义返回结果集
	 * @param field0,field1
	 * @return
	 */
	public void _Result(Object field0,Object field1);
	/**
	 * 定义返回结果集
	 * @param field0,field1,field2
	 * @return
	 */
	public void _Result(Object field0,Object field1,Object field2);
	/**
	 * 定义返回结果集
	 * @param field0,field1,field2,field3
	 * @return
	 */
	public void _Result(Object field0,Object field1,Object field2,Object field3);
	/**
	 * 定义返回结果集
	 * @param field0,field1,field2,field3,field4
	 * @return
	 */
	public void _Result(Object field0,Object field1,Object field2,Object field3,Object field4);
	/**
	 * 是否存在结果集定义
	 * @return
	 */
	public boolean _HasResult();
	/**
	 * 数据库保存对象操作。
	 * 如果存在，执行修改。否则，执行插入。
	 * 如果对象主键数据不为空，按主键进行查询，判断是否存在。
	 * 如果对象其他数据项不为空，按其他项并集查询，判断是否存在。
	 * @return
	 * @throws SQLException
	 */
	public AutopersObject _Save()  throws SQLException;
	/**
	 *数据库删除。
	 * 如果对象主键数据不为空，按照主键删除，否则以其他不为空数据项进行删除。
	 * @return
	 * @throws SQLException
	 */
	public Integer _Delete()  throws SQLException;
	/**
	 *清空对象数据。
	 * 包括属性值、分页对象和查询对象
	 * @throws Exception
	 */
	public void _Clear()  throws Exception;
	/**
	 * 数据库删除。
	 * 按照查询对象进行删除操作
	 * @param autoPersQuery
	 * @return
	 * @throws SQLException
	 */
	public Integer _Delete(AutopersQuery autoPersQuery)  throws SQLException;
	/**
	 * 批量删除多条数据，按照object对象的属性设置有无删除
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	public Long _Delete(List<?> list)  throws SQLException;
	/**
	 * 查询,如果存在Query 则进行Query查询，否则对象属性查询。
	 *
	 * @return
	 * @throws SQLException
	 */
	public List<AutopersObject> _Data()  throws SQLException;

	/**
	 * 查询,如果存在Query 则进行Query查询，否则对象属性查询。
	 * @param codeId
	 * @return
	 * @throws SQLException
	 */
	public List<AutopersObject> _Data(String codeId)  throws SQLException;
	/**
	 * 按照查询对象进行查询
	 * @param autoPersQuery
	 * @return
	 * @throws SQLException
	 */
	public List<AutopersObject> _Data(AutopersQuery autoPersQuery)  throws SQLException;
	/**
	 * 查询,如果存在Query 则进行Query查询，否则对象属性查询。
	 *
	 * @return
	 * @throws SQLException
	 */
	public Integer _Count()  throws SQLException;
	/**
	 * 查询
	 * @param autoPersQuery
	 * @return
	 * @throws SQLException
	 */
	public Integer _Count(AutopersQuery autoPersQuery)  throws SQLException;

	public boolean _IsCombine();
	/**
	 * 获取分页设置
	 * @return
	 */
	public AutopersQueryPage _Page();
	/**
	 * 设置分页设置
	 * @param queryPage
	 */
    public void _Page(AutopersQueryPage queryPage);
	/**
	 * 设置分页设置
	 * @param page,num
	 */
	public void _Page(int page,int num);
	/*
	 * 获取类的xml映射类
	 */
	public Map<String,Class<?>> _GetMappingClass();
	/**
	 * 获取代理类
	 * @return
	 */
	public Class<?> _GetProxyClass();
	/**
	 * 获取类的xml映射对象
	 * @return
	 */
	public Map<String,AutopersMappingClass> _GetMapping();

	public void _PutField(AutopersObjectField field);

	/**
	 * 获取所有的属性
	 * @return
	 */
	public HashMap<String, AutopersObjectField> _GetFields();
	/**
	 * 构建并获取查询对象
	 * @return
	 */
	public AutopersQuery _Query();

	public String _QueryId();
	public void _QueryId(String queryId);

	public Map<String,CombineDictionaryObject> _GetCombineDictObjMap();

	public String _AutopersObjectId();

	/**
	 * 获取对象的mapping xml定义
	 * @return 包括列名、列备注、列类型等等
	 */
	public AutopersMappingClass _Mapping();

	public boolean equals(Object object);


}

package org.violetime.autopers.query;

import java.util.List;

import org.violetime.autopers.function.AutopersFunctionSingle;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;

public interface AutopersQuery {
	
	/**
	 * 创建两个实体类的绑定关系
	 * @return
	 */
	public AutopersQuery combine(AutopersObject combineObject);
	public AutopersQuery _Equals(Object field,Object value);
	public AutopersQuery _Unequals(Object field,Object value);
	public AutopersQuery _Greater(Object field,Object value);
	public AutopersQuery _Less(Object field,Object value);
	/**
	 * 大于等于
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersQuery _GreaterEqual(Object field,Object value);
	/**
	 * 小于等于
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersQuery _LessEqual(Object field,Object value);
	public AutopersQuery _Like(Object field,Object value);
	public AutopersQuery _LikeStart(Object field,Object value);
	public AutopersQuery _LikeEnd(Object field,Object value);
	public AutopersQuery _In(Object field,List<String> values);
	public AutopersQuery _Between(Object field,Object start,Object end);
	public AutopersQuery _OrderBy(Object field,Boolean desc);
	public AutopersQuery _OrderBy(Object fieldA,Object fieldB,Boolean desc);
	public AutopersQuery _GroupBy(Object field);
	public AutopersQuery _GroupBy(Object fieldA,Object fieldB);

	/**
	 * 或者，_Or连接的前后两个条件为或者关系
	 * @return
	 */
	public AutopersQuery _Or();

	/**
	 * 或者，_Or前面的条件与内部的查询句为或者关系
	 * @param query
	 * @return
	 */
	public AutopersQuery _Or(AutopersQuery query);

	/**
	 * 或者，两个查询语句为或者关系
	 * @param queryA
	 * @param queryB
	 * @return
	 */
	public AutopersQuery _Or(AutopersQuery queryA,AutopersQuery queryB);




	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersFunctionSingle _Equals_F(Object field, Object value);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersFunctionSingle _Unequals_F(Object field,Object value);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersFunctionSingle _Greater_F(Object field,Object value);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersFunctionSingle _LessEqual_F(Object field,Object value);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersFunctionSingle _GreaterEqual_F(Object field,Object value);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersFunctionSingle _Less_F(Object field,Object value);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param value
	 * @return
	 */
	public AutopersFunctionSingle _Like_F(Object field,Object value);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param values
	 * @return
	 */
	public AutopersFunctionSingle _In_F(Object field,List<String> values);
	/**
	 * AutopersFunctionSingle 函数作用于field
	 * @param field
	 * @param start
	 * @param end
	 * @return
	 */
	public AutopersFunctionSingle _Between_F(Object field,Object start,Object end);
	/**
	 * AutopersFunctionSingle 函数作用于field,例如  order by function(field) desc
	 * @param field
	 * @param desc
	 * @return
	 */
	public AutopersFunctionSingle _OrderBy_F(Object field,Boolean desc);
	/**
	 * AutopersFunctionSingle 函数作用于field,例如 group function(field)
	 * @param field
	 * @return
	 */
	public AutopersFunctionSingle _GroupBy_F(Object field);

	/**
	 * 增加属性序列
	 * @param objectField
	 */
	public void addField(AutopersObjectField objectField);

	/**
	 * 性序列
	 */
	public List<AutopersObjectField> getFields();
	/**
	 * 清除当前的属性序列
	 */
	public void clearFields();
	/**
	 * 清除查询,返回初始
	 */
	public void clear();
	/**
	 * 获取以构建的查询方法
	 * @return
	 */
	public List<AutopersQueryMethod> getMethods();
	/**
	 * 获取以构建的查询方法
	 * @return
	 */
	public AutopersQueryMethod getMethod(String fieldName,AutopersQueryMethods method);
	/**
	 * 获取要查询的对象
	 * @return
	 */
	public AutopersObject getAutopersObject();
	public void setAutopersObject(AutopersObject autopersObject);
	
}

package org.violetime.autopers.objects;

import org.violetime.autopers.query.AutopersQuery;
/**
 * 数据库实体类 组合类的接口
 * @author taoyo
 *
 */
public interface AutopersObjectJoin {

	/**
	 * 获取多个组合实体类之间的字段连接关系。
	 * @param autoPersObjectField
	 * @return
	 */
	public AutopersQuery equalsField(AutopersObjectField autoPersObjectField);
}

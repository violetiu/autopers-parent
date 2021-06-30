package org.violetime.autopers.datatype;

import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;

/*
 * 根据字段的数据类型定义，生产对应的sql值
 */
public interface AutopersSqlType {

	/**
	 * 获取sql值
	 * @param mappingField
	 * @param obj
	 * @return
	 */
	public String getSqlVal(IAutopersMappingField mappingField, Object obj);
}

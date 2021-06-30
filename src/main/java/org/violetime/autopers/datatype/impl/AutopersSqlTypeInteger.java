package org.violetime.autopers.datatype.impl;

import org.violetime.autopers.datatype.AutopersSqlType;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;

public class AutopersSqlTypeInteger implements AutopersSqlType{

	@Override
	public String getSqlVal(IAutopersMappingField mappingField, Object obj) {
		// TODO Auto-generated method stub
		String value = obj.toString();
		if(value.matches("^-?\\d+$"))
			return  value ;
		return null;
	}

}

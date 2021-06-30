package org.violetime.autopers.datatype.impl;

import org.violetime.autopers.datatype.AutopersSqlType;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;

import java.math.BigDecimal;

public class AutopersSqlTypeDouble implements AutopersSqlType{

	@Override
	public String getSqlVal(IAutopersMappingField mappingField, Object obj) {
		// TODO Auto-generated method stub
		BigDecimal decimal=new BigDecimal(obj.toString());
		String value = decimal.toPlainString();
		if(value.matches("^-?\\d+(\\.\\d+)?$"))
			return value;
		return null;
	}

}

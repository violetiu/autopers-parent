package org.violetime.autopers.datatype.impl;

import org.violetime.autopers.datatype.AutopersSqlType;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.units.AutopersObjectsUnit;

public class AutopersSqlTypeString implements AutopersSqlType {

	@Override
	public String getSqlVal(IAutopersMappingField mappingField, Object obj) {
		// TODO Auto-generated method stub
		if(obj==null)
			return null;
		String value= AutopersObjectsUnit.transactSQLInjection(obj);
		return "'"+value+"'";
	}

}

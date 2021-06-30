package org.violetime.autopers.generator;

import java.util.UUID;

import org.violetime.autopers.mapping.AutopersMappingField;

/**
 * 主键生成策略
 */
public class Native implements Generator {
	private AutopersMappingField field;
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		if(field.getJavatype().equals(String.class.getName())){
			return UUID.randomUUID().toString();
		}
		return null;
	}
	@Override
	public void setField(AutopersMappingField field) {
		// TODO Auto-generated method stub
		this.field=field;
	}
}

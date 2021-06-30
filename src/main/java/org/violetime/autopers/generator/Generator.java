package org.violetime.autopers.generator;

import org.violetime.autopers.mapping.AutopersMappingField;

public interface Generator {

	public String getValue();
	public void   setField(AutopersMappingField field);
}

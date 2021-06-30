package org.violetime.autopers.mapping;

import org.violetime.autopers.generator.Generator;

public class AutopersMappingField implements IAutopersMappingField {

	
	private AutopersMappingGenerator mappingGenerator;
	private Generator generator;
	public Generator getGenerator() {
		if(generator==null)
			return null;
		generator.setField(this);
		return generator;
	}
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}
	private AutopersMappingForeign mappingForeign;
	private String name,comment, column, jdbctype,  label,javatype,primary;
	
	public String getPrimary() {
		return primary;
	}
	public void setPrimary(String primary) {
		this.primary = primary;
	}
	public AutopersMappingGenerator getMappingGenerator() {
		return mappingGenerator;
	}
	public void setMappingGenerator(AutopersMappingGenerator mappingGenerator) {
		this.mappingGenerator = mappingGenerator;
	}
	public AutopersMappingForeign getMappingForeign() {
		return mappingForeign;
	}
	public void setMappingForeign(AutopersMappingForeign mappingForeign) {
		this.mappingForeign = mappingForeign;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getJdbctype() {
		return jdbctype;
	}
	public void setJdbctype(String jdbctype) {
		this.jdbctype = jdbctype;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getJavatype() {
		return javatype;
	}
	public void setJavatype(String javatype) {
		this.javatype = javatype;
	}
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}

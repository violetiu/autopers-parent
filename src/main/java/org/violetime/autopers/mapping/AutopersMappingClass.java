package org.violetime.autopers.mapping;

import java.util.List;

public class AutopersMappingClass implements IAutopersMappingClass{

	private List<IAutopersMappingField> fields;
	private String name;
	private String label;
	private String conmment;
	private String classPath;
	private String source;
	private String updatetime;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public List<IAutopersMappingField> getFields() {
		return fields;
	}
	public IAutopersMappingField getField(String fieldName) {
		for (IAutopersMappingField field : fields) {
			if(field.getName().equals(fieldName))
				return field;
		}
		return null;
	}
	public void setFields(List<IAutopersMappingField> fields) {
		this.fields = fields;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getConmment() {
		return conmment;
	}
	public void setConmment(String conmment) {
		this.conmment = conmment;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	
}

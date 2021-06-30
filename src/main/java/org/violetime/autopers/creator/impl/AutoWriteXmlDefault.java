package org.violetime.autopers.creator.impl;

import org.violetime.autopers.database.DataBaseColumn;
import org.violetime.autopers.database.DataBaseTable;
import org.violetime.autopers.units.AutopersCodeName;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
public class AutoWriteXmlDefault {
	private static HashMap<String, String> dataTypeMap;
	public static HashMap<String, String> getDataTypeMap() {
		return dataTypeMap;
	}
	public static void setDataTypeMap(HashMap<String, String> dataTypeMap) {
		AutoWriteXmlDefault.dataTypeMap = dataTypeMap;
	}
	public static String autoWrite(List<DataBaseColumn> baseColumns,
			DataBaseTable baseTable, String path,String classPackage,String baseSource) throws Exception {
		String className;
		File mappingPath=new File(path);
		if(!mappingPath.exists()){
			mappingPath.mkdir();
		}
		String classPath;
		if (path.contains("\\"))
			path = path.replace("\\", "/");
		if (path.contains("%20"))
			path = path.replace("%20", " ");
		className = AutopersCodeName.className(baseTable.getTableName().toLowerCase(),baseSource);
		if (path.endsWith("/")) {
			classPath = path + "" + className + ".xml";
		} else {
			classPath = path + "/" + className + ".xml";
		}
		FileWriter fileWriter = new FileWriter(new File(classPath));
		fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		fileWriter.write("\n");
		fileWriter.write("<autopers-mapping>");
		fileWriter.write("\n");
		fileWriter.write("\t<class name=\""+baseTable.getTableName()+"\" source=\""+baseSource+"\" classPath=\""+classPackage+"."+className+"\" label=\"\" comment=\""+baseTable.getComment()+"\" updatetime=\""+baseTable.getUpdateTime()+"\">");
		fileWriter.write("\n");
		for(DataBaseColumn baseColumn:baseColumns){
			String primary="";
			if(baseColumn.getPrimary()!=null)
				primary=baseColumn.getPrimary();
			fileWriter.write("\t\t<field name=\""+AutopersCodeName.attributeName(baseColumn.getColnumName().toLowerCase())+"\" comment=\""+baseColumn.getComment()+"\" column=\""+baseColumn.getColnumName()+"\" jdbctype=\""+baseColumn.getJdbcType()+"\"  label=\"\" javatype=\""+dataTypeMap.get(baseColumn.getJdbcType().toUpperCase())+"\" primary=\""+primary+"\" >");
			fileWriter.write("\n");
			if(baseColumn.getPrimary()!=null){
				fileWriter.write("\t\t\t<generator class=\"org.violetime.autopers.generator.Native\"></generator>");
			}
			fileWriter.write("\n");
			if(baseColumn.getRefTable()!=null){
				fileWriter.write("\t\t\t<foreign name=\"region\" table=\""+baseColumn.getRefTable()+"\" column=\""+baseColumn.getRefColumn()+"\" type=\"0\"></foreign>");
			}
			fileWriter.write("\n");
			fileWriter.write("\t\t</field>\n");
		}
		fileWriter.write("\n");
		fileWriter.write("\t</class>");
		fileWriter.write("\n");
		fileWriter.write("</autopers-mapping>");
		fileWriter.flush();
		fileWriter.close();
		return null;
	}
}

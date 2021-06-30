package org.violetime.autopers.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;


import org.violetime.autopers.session.objects.QueryObject;

public class AutopersMapping {

	private final static Logger logger = Logger.getLogger(QueryObject.class.getName());
	/**
	 * classPath,AutopersMappingClass
	 */
	private static HashMap<String, AutopersMappingClass> classMap=new HashMap<>();
	/**
	 * 获取实体类配置文件
	 * @param cls
	 * @return
	 */
	public static AutopersMappingClass getMappingClassXml(Class cls){
	//	logger.debug("getMappingClassXml class is- "+cls.getName());
		if(classMap!=null){
		  return 	classMap.get(cls.getName());
		}
		return null;
	}
	/**
	 * 获取实体类配置文件
	 * @param tableName
	 * @return
	 */
	public static AutopersMappingClass getMappingClassXml(String tableName){
		
		if(classMap==null)
			return null;
		for(Object key:classMap.keySet()){
			AutopersMappingClass  mappingClass=classMap.get(key);
			if(mappingClass.getName().equals(tableName)){
				return mappingClass;
			}
		}
		
		return null;
	}
	public static void addMappingClass(AutopersMappingClass mappingClass){
		if(classMap==null)
			classMap=new HashMap<>();
		classMap.put(mappingClass.getClassPath(), mappingClass);
		
	}
	/**
	 * 
	 * @return
	 */
	public static List<AutopersMappingClass> getMappingClass(){
		List<AutopersMappingClass> result=new ArrayList<AutopersMappingClass>();
		for(AutopersMappingClass mappingClass: classMap.values()) {
			result.add(mappingClass);
		}
		
		return result;
	}
}

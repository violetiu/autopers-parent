package org.violetime.autopers.units;

import org.violetime.autopers.cache.AutopersCache;
import org.violetime.autopers.datatype.AutopersSqlType;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersCombine;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.reflect.AutopersReflectClass;

import java.math.BigDecimal;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutopersObjectsUnit {

	private final static Logger logger=Logger.getLogger(AutopersObjectsUnit.class.getName());

    /**
	 * 获取父类，
	 * @param cls
	 * @return
	 */
	public static Class getObjectXmlClass(Class cls) {
		String clsName = cls.getName();
		if(cls.getInterfaces()==null||cls.getInterfaces().length<=0)
			return null;
		for(Class inter : cls.getInterfaces()){
			if(inter.getName().equals(AutopersObject.class.getName())){
				return cls;
			}
		}
		Class resut=null;
		for(Class inter : cls.getInterfaces()){
			if( getObjectXmlClass(inter)!=null){
				resut=inter;
				continue;
			}
		
		}
		return resut;
	}
	/**
	 * 是否实现了组合类实体。
	 * @param cls
	 * @return true：组合实体类，false：单个实体类，null：非autopersobject
	 */
	public   static Boolean isCombine(Class cls){
		boolean isCombine=false;
		boolean isAutopersObject=false;
		if(cls.getInterfaces()!=null)
			for(Class<?> inter:cls.getInterfaces()) {
				if(inter.equals(AutopersCombine.class))
					{
						isCombine=true;
						if(inter.getInterfaces()!=null) {
							for(Class<?> temp:inter.getInterfaces()) {
								if(temp.getClass().equals(AutopersObject.class)) 
									{
										isAutopersObject=true;
										break;
									}
							}
						}
					}else if(inter.equals(AutopersObject.class)) {
						isAutopersObject=true;
						break;
					}
				
			}
		if(isCombine&&isAutopersObject)
			return true;
		if(!isCombine&&isAutopersObject)
			return false;
		return null;
	}

	/**
	 * 按照名称获取属性mapping，从mapping class获取mappingFields
	 * @param field
	 * @param mappingClass
	 * @param mappingFields
	 * @return
	 */
	public static IAutopersMappingField getMappingField(String field, AutopersMappingClass mappingClass,Set<IAutopersMappingField> mappingFields){
		//System.out.println("-----"+field);
		if( mappingClass!=null){
			//System.out.println("mappingClass");
			for(IAutopersMappingField mappingField: mappingClass.getFields()){
				if(mappingField.getName().equals(field)){

					return mappingField;
				}
			}
		}
		if(mappingFields!=null){
			//	System.out.println("mappingFields");
			for(IAutopersMappingField mappingField: mappingFields){
				if(mappingField.getName().equals(field)){
					return mappingField;
				}
			}
		}

		return  null;
	}
	/**
	 * 根据对象属性的类型，得到object相应的sql格式字符串
	 * @param mappingField
	 * @param object
	 * @return
	 */
    public static String getSqlValueByField(IAutopersMappingField mappingField, Object object){
		if(object==null||mappingField==null||mappingField.getJavatype()==null||mappingField.getJdbctype()==null)
		{
			logger.log(Level.FINE,"return null  when object is null or mappingField is null");
			return null;
		}
		String typeClass="org.violetime.autopers.datatype.impl.AutopersSqlType"+mappingField.getJavatype().substring(mappingField.getJavatype().lastIndexOf(".")+1);
		try{
			Object cache=AutopersCache.peek(typeClass);
			AutopersSqlType persSqlType=null;
			if(cache!=null){
				persSqlType= (AutopersSqlType) cache;
			}else{
				persSqlType= (AutopersSqlType) AutopersReflectClass.getObject(typeClass);
				AutopersCache.push(typeClass,persSqlType);
			}
			return persSqlType.getSqlVal(mappingField, object);
		}catch(Exception exception){
			exception.printStackTrace();
			logger.log(Level.FINE,"没有对应的数据类型处理对象");
		}
		return null;
	}

	/**
	 * 防止sql注入
	 * @param object
	 * @return
	 */
	public static String transactSQLInjection(Object object)
	{
		if(object==null)
			return "null";
		return object.toString().replaceAll(".*([';]+|(--)+).*", " ");
	}
	/**
	 * java值转sql值
	 * @param object
	 * @param mappingField
	 * @return
	 */
	public static String dealSqlValue(Object object, IAutopersMappingField mappingField) {
		if (object == null || mappingField == null)
			return null;
		String value = "";
		if (mappingField.getJavatype() == null) {
			value =transactSQLInjection( object.toString());
			return "'" + value + "'";
		} else if (mappingField.getJavatype().equals("java.lang.String")) {
			value =transactSQLInjection( object.toString());
			return "'" + value + "'";
		} else if(mappingField.getJavatype().equals("java.lang.Lang")) {
			value = object.toString();
			if(value.matches("^-?\\d+$"))
				return  value ;
			return null;
			
		}else if(mappingField.getJavatype().equals("java.lang.Integer")) {
			value = object.toString();
			if(value.matches("^-?\\d+$"))
				return  value ;
			return null;
			
		}else if(mappingField.getJavatype().equals("java.lang.Double")) {

			if(object.toString().equals("NaN")||object.toString().equals("Infinity")){
				logger.info(object.toString()+" 不是数字!");
			}
			BigDecimal decimal=new BigDecimal(object.toString());
			value = decimal.toPlainString();
			if(value.matches("^-?\\d+(\\.\\d+)?$"))
				return value;
			return null;
			
		}else if(mappingField.getJavatype().equals("java.util.Date")) {
			value = object.toString();
			return "TO_DATE('" + value + "','YYYY-MM-DD HH24:MI:SS')";
		}
		else {
			value =transactSQLInjection( object.toString());
			return "'" + value + "'";
		}

	}
}
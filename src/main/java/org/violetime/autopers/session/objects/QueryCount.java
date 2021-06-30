package org.violetime.autopers.session.objects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectsFactory;
import org.violetime.autopers.session.tool.AutopersQueryTool;
import org.violetime.autopers.units.PlatformInvokeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.platform.AutopersPlatformPackage;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.query.AutopersQueryMethod;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.units.AutopersObjectsUnit;

public class QueryCount implements AutopersPlatformInvoke {
	private final static Logger logger = Logger.getLogger(Query.class.getName());
	private AutopersSession persSession;
	private Object[] args;
	private AutopersPlatform platform;

	@Override
	public Object invoke() throws Throwable {

		AutopersQuery query = (AutopersQuery) args[0];
		AutopersObject autoPersObject = query.getAutopersObject();
		Map<String,Class<?>> xmlClassMap= autoPersObject._GetMappingClass();
		Map<String,AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();
		int result=0;
		if(!autoPersObject._IsCombine()) {
			Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
			AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
			if (mappingClass != null) {
				StringBuffer queryMethodBuffer=AutopersQueryTool.getQuerySQL(query,platform,persSession,mappingClass,false);
				List<IAutopersMappingField> mappingFields = mappingClass.getFields();
				if (mappingFields == null || mappingFields.size() == 0) {
					logger.log(Level.FINE,"没有获取到mappingFields");
				}
				String sql = "select count(*) from " + mappingClass.getName()+" "+autoPersObject._GetProxyClass().getSimpleName();
				if (queryMethodBuffer.length() > 0) {
					if(queryMethodBuffer.toString().trim().startsWith("order by ")||queryMethodBuffer.toString().trim().startsWith("group by"))
						sql += " " + queryMethodBuffer.toString();
					else
						sql += " where  " + queryMethodBuffer.toString();
				}

				logger.log(Level.FINE,sql);
				PreparedStatement preparedStatement = persSession.getPreparedStatement();
				ResultSet resultSet = preparedStatement.executeQuery(sql);

				if (resultSet.next()) {
					result= resultSet.getInt(1);
				}
				preparedStatement.close();
			}
		}else{
//主查询语句
			//使用被组合的对象表使用联合查询 select .. from a,b where a.key =b.key
			StringBuffer querySql=new StringBuffer("select count(*)  ");

			Map<String,IAutopersMappingField> fieldMap=new HashMap<>();
			Map<String,Class> classMap=new HashMap<>();
			Map<String,AutopersMappingClass> mappingMap=new HashMap<>();
			for(Object key :xmlClassMap.keySet()){
				Class<?> xmlClass = xmlClassMap.get(key);
				AutopersMappingClass mappingClass =  mappingClassMap.get(key);
				if (mappingClass != null) {

					for(IAutopersMappingField mappingField:mappingClass.getFields()) {
						if(fieldMap.get(mappingField.getColumn())==null){
							fieldMap.put(mappingField.getColumn(),mappingField);
							classMap.put(mappingField.getColumn(),xmlClass);
							mappingMap.put(mappingField.getColumn(),mappingClass);
						}
					}
				}else{
					logger.log(Level.FINE,"mappingClass is null");
				}
			}

			Set<IAutopersMappingField> fieldSet=new HashSet<>();
			for(Object key :fieldMap.keySet()){
				IAutopersMappingField  field=fieldMap.get(key);
				Class<?> xmlClass = classMap.get(key);
				AutopersMappingClass mappingClass =  mappingMap.get(key);
				fieldSet.add(field);


			}


			querySql.append( " from  " );
			for(Object key :xmlClassMap.keySet()){
				Class<?> xmlClass = xmlClassMap.get(key);
				AutopersMappingClass mappingClass =  mappingClassMap.get(key);
				querySql.append(mappingClass.getName()+" "+xmlClass.getSimpleName()+",");

			}
			if(querySql.toString().endsWith(","))
				querySql=new StringBuffer(querySql.substring(0,querySql.length()-1));
			//
			querySql.append(" where ");
			for(Object key :fieldMap.keySet()){
				IAutopersMappingField  field=fieldMap.get(key);
				Class<?> xmlClass = classMap.get(key);
				AutopersMappingClass mappingClass =  mappingMap.get(key);



				AutopersObjectField objectField=  autoPersObject._GetFields().get(field.getName());
				if(objectField!=null&&objectField.isCombine()){
					querySql.append(" " + xmlClass.getSimpleName() + "." + field.getColumn()+ "=" + objectField.getCombineField() + " and");
				}else{
					logger.log(Level.FINE,"objectField is null");
				}



			}

			//主查询语句的条件
			StringBuffer queryMethodBuffer=AutopersQueryTool.getQuerySQL(query,platform,persSession,null,fieldSet,true);
			if (queryMethodBuffer.length() > 0) {

				querySql.append(" " + queryMethodBuffer.toString());

			}
			if(querySql.toString().endsWith("and")){
				querySql=new StringBuffer(querySql.substring(0,querySql.length()-3));
			}

			if (autoPersObject._Page() != null) {
				querySql=new StringBuffer( autoPersObject._Page().getSql(querySql.toString(),platform));
			}
			logger.log(Level.FINE,querySql.toString());
			//开始执行查询方法
			PreparedStatement preparedStatement = persSession.getPreparedStatement();
			ResultSet resultSet = preparedStatement.executeQuery(querySql.toString());

			if (resultSet.next()) {
				result= resultSet.getInt(1);
			}
			preparedStatement.close();
		}
		return result;
	}


	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
}
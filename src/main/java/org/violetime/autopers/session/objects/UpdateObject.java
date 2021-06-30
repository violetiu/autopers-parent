package org.violetime.autopers.session.objects;

import java.sql.PreparedStatement;
import java.util.Map;

import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.mapping.IAutopersMappingField;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.tool.AutopersPartTool;
import org.violetime.autopers.units.AutopersObjectsUnit;

/**
 * @author Administrator
 *
 */
public class UpdateObject implements AutopersPlatformInvoke {
	private final static Logger logger=Logger.getLogger(UpdateObject.class.getName());
	private AutopersSession session;
	private Object[] args;
	private DataBaseSource baseSource;
	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		AutopersObject autoPersObject=(AutopersObject)args[0];
		Map<String,Class<?>> xmlClassMap= autoPersObject._GetMappingClass();
		Map<String,AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();

		if(!autoPersObject._IsCombine()) {
			Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
			AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
			if (mappingClass != null) {
				StringBuffer sqlBuffer = new StringBuffer();
				StringBuffer primaryBuffer = new StringBuffer();
				for (IAutopersMappingField mappingField : mappingClass.getFields()) {
					AutopersObjectField objectField=null;
					if(autoPersObject._GetFields()!=null){
						objectField=autoPersObject._GetFields().get(mappingField.getName());
					}
					if(objectField==null)
						continue;
					Object reflectResult = null;
					if(objectField!=null){
						reflectResult=objectField.getValue();
					}

					String value =AutopersObjectsUnit.dealSqlValue(reflectResult, mappingField);
					if (mappingField.getPrimary()==null||mappingField.getPrimary().length()==0) {
						if(value==null||value.trim().equals("''"))
							sqlBuffer.append(" ,  " + mappingField.getColumn()+" = null ");
						else
							sqlBuffer.append(" ,  " + mappingField.getColumn()+" = "+value);
					}
					if(mappingField.getPrimary()!=null&&primaryBuffer!=null){
						if(value==null||value.trim().equals("''")||value.length()==0){
							primaryBuffer=null;
						}else{
							primaryBuffer.append(" and  " + mappingField.getColumn()+" = "+value);
						}
					}
				}

				if(primaryBuffer==null||primaryBuffer.length()<=0){
					//TODO
					return null;
				}else if(sqlBuffer.length()==0){
					//TODO
					return null;
				}
				String tableName= AutopersPartTool.getPartTable(mappingClass,autoPersObject,autoPersObject,baseSource);
				String sql="update "+ tableName+" set "+sqlBuffer.substring(3)+" where  "+primaryBuffer.substring(5);
				logger.log(Level.FINE,sql);
				PreparedStatement preparedStatement =session.getPreparedStatement();
				int count=preparedStatement.executeUpdate(sql);
				preparedStatement.close();
				if(count>0)
					return autoPersObject;
			}

		}else{





		}

		return null;
	}
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}

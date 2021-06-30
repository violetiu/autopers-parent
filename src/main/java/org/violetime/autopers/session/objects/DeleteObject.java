package org.violetime.autopers.session.objects;

import java.sql.PreparedStatement;
import java.util.Map;

import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.mapping.IAutopersMappingField;
import java.util.logging.Logger;

import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.tool.AutopersPartTool;
import org.violetime.autopers.units.AutopersObjectsUnit;

public class DeleteObject implements  AutopersPlatformInvoke {
	private final static Logger logger=Logger.getLogger(DeleteObject.class.getName());
	private AutopersSession autoPersSession;
	private Object[] args;
	private DataBaseSource baseSource;
    @Override
    public Object invoke()
            throws Throwable {
        AutopersObject  autoPersObject=(AutopersObject)args[0];
		Map<String,Class<?>> xmlClassMap= autoPersObject._GetMappingClass();
		Map<String,AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();

		if(!autoPersObject._IsCombine()){
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
					Object reflectResult = null;
					if(objectField!=null){
						reflectResult=objectField.getValue();
					}
					String value = AutopersObjectsUnit.dealSqlValue(reflectResult, mappingField);
					if (value != null) {
						if(value.trim().equals("''"))
							sqlBuffer.append(" and  " + mappingField.getColumn()+" is null ");
						else
							sqlBuffer.append(" and  " + mappingField.getColumn()+" = "+value);
					}
					if(mappingField.getPrimary()!=null&&primaryBuffer!=null){
						if(value==null||value.trim().equals("''")||value.length()==0){
							primaryBuffer=null;
						}else{
							primaryBuffer.append(" and  " + mappingField.getColumn()+" = "+value);
						}
					}
				}
				String tableName= AutopersPartTool.getPartTable(mappingClass,autoPersObject,autoPersObject,baseSource);
				String sql="delete from "+ tableName;
				if(primaryBuffer!=null&&primaryBuffer.length()>0){
					sql+=" where "+primaryBuffer.substring(5);
				}else if(sqlBuffer.length()>0){
					sql+=" where "+sqlBuffer.substring(5);
				}else{
					sql="truncate table "+mappingClass.getName();
				}
				logger.info(sql);
				PreparedStatement preparedStatement =autoPersSession.getPreparedStatement();
				int count=preparedStatement.executeUpdate(sql);
				preparedStatement.close();
				return count;
			}

		}else{



		}

		return 0;
    }
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
    


}
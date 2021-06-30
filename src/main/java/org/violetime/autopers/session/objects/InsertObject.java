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
import org.violetime.autopers.objects.AutopersObjectsFactory;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.tool.AutopersPartTool;
import org.violetime.autopers.units.AutopersObjectsUnit;

public class InsertObject implements AutopersPlatformInvoke {
	private final static Logger logger=Logger.getLogger(InsertObject.class.getName());
	private AutopersSession autoPersSession;
	private Object[] args;
	private DataBaseSource baseSource;
	@Override
	public Object invoke() throws Throwable {
		AutopersObject autoPersObject = (AutopersObject) args[0];
		Map<String,Class<?>> xmlClassMap= autoPersObject._GetMappingClass();
		Map<String,AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();

		if(!autoPersObject._IsCombine()) {
			Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
			AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
			StringBuffer colnumsBuffer = new StringBuffer();
			StringBuffer valuesBuffer = new StringBuffer();
			for (IAutopersMappingField mappingField : mappingClass.getFields()) {
				if(autoPersObject._GetFields()==null)
					continue;
				AutopersObjectField autoPersObjectField= autoPersObject._GetFields().get(mappingField.getName());

				String value =null;
				if(autoPersObjectField!=null&&autoPersObjectField.getValue()!=null)
					value=AutopersObjectsUnit.dealSqlValue(autoPersObjectField.getValue(), mappingField);
				if (value != null) {
					colnumsBuffer.append("," + mappingField.getColumn());
					valuesBuffer.append("," + value);
				}
				if (value == null && mappingField.getGenerator() != null) {
					value = mappingField.getGenerator().getValue();
					try {
						AutopersObjectField objectField=AutopersObjectsFactory.newInstanceField(value, mappingField.getName(), xmlClass.getName());
						autoPersObject._GetFields().put(mappingField.getName(), objectField);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					colnumsBuffer.append("," + mappingField.getColumn());
					valuesBuffer.append(",'" + value + "'");
				}
			}
			String tableName= AutopersPartTool.getPartTable(mappingClass,autoPersObject,autoPersObject,baseSource);
			String sql = "insert into " + tableName + "(" + colnumsBuffer.substring(1) + ") values("
					+ valuesBuffer.substring(1) + ")";
			logger.log(Level.FINE,sql);
			PreparedStatement preparedStatement = autoPersSession.getPreparedStatement();
			int count = preparedStatement.executeUpdate(sql);
			preparedStatement.close();
			if (count > 0)
				return autoPersObject;
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
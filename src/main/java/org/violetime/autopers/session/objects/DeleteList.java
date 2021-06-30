package org.violetime.autopers.session.objects;

import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.tool.AutopersPartTool;
import org.violetime.autopers.units.AutopersObjectsUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteList implements  AutopersPlatformInvoke {
	private final static Logger logger=Logger.getLogger("Autopers");
	private AutopersSession autoPersSession;
	private Object[] args;
	private DataBaseSource baseSource;

	/**
	 *	public Integer deleteList(List<?> list, AutopersObject object) throws SQLException;
	 * @return
	 * @throws Throwable
	 */
    @Override
    public Object invoke()
            throws Throwable {
		AutopersObject autoPersObject = (AutopersObject) args[1];
		List<AutopersObject> list = (List<AutopersObject>) args[0];
		Map<String, Class<?>> xmlClassMap = autoPersObject._GetMappingClass();
		Map<String, AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();

		if (autoPersObject._GetFields() == null || autoPersObject._GetFields().size() == 0) {
			logger.info("删除对象未设置属性");
			return 0;
		}
		if (!autoPersObject._IsCombine()) {
			Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
			AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
			if (mappingClass == null){
				logger.info("删除对象为发现映射文件");
				return 0;
			}

			List<String> sqls = new ArrayList<>();
			for (AutopersObject persObject : list) {
				StringBuffer sqlBuffer = new StringBuffer();
				for (Object fieldName : autoPersObject._GetFields().keySet()) {
					AutopersObjectField objectField = persObject._GetFields().get(fieldName);
					IAutopersMappingField mappingField = mappingClass.getFields().stream().filter(field -> (field.getName().equals(fieldName))).findFirst().get();
					Object reflectResult = null;
					if (objectField != null) {
						reflectResult = objectField.getValue();
					}
					String value = AutopersObjectsUnit.dealSqlValue(reflectResult, mappingField);
					String sql = "";
					if (value != null) {
						if (value.trim().equals("''"))
							sql = (" " + mappingField.getColumn() + " is null ");
						else
							sql = (" " + mappingField.getColumn() + " = " + value);
					} else {
						sql = (" " + mappingField.getColumn() + " is null ");
					}
					if (sqlBuffer.length() == 0) {
						sqlBuffer.append(sql);
					} else {
						sqlBuffer.append(" and ").append(sql);
					}
				}
				if (sqlBuffer.length() > 0) {
					String tableName= AutopersPartTool.getPartTable(mappingClass,autoPersObject,persObject,baseSource);
					sqls.add("delete from " + mappingClass.getName() + " where " + sqlBuffer.toString());
				}
			}

			Connection connection = autoPersSession.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement preparedStatement = connection.prepareStatement(" ");
			for(String sql:sqls){
				logger.log(Level.FINE,sql);
				preparedStatement.addBatch(sql);
			}
			logger.info("Delete data");
			long count=0;
			long[] counts = preparedStatement.executeLargeBatch();
			preparedStatement.close();
			connection.commit();
			for (long num : counts) {
				count += num;
			}
			logger.info("Delete data is complete :"+count+"n");
			return count;


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
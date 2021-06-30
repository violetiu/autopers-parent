package org.violetime.autopers.session.objects;

import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.part.AutopersPart;
import org.violetime.autopers.objects.part.AutopersPartFactory;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.platform.AutopersPlatformPackage;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.query.AutopersQueryMethod;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.tool.AutopersPartTool;
import org.violetime.autopers.session.tool.AutopersQueryTool;
import org.violetime.autopers.units.AutopersObjectsUnit;
import org.violetime.autopers.units.PlatformInvokeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteQuery implements  AutopersPlatformInvoke {
	private final static Logger logger=Logger.getLogger(DeleteQuery.class.getName());
	private Object[] args;
	private AutopersPlatform platform;
	private AutopersSession persSession;
	private DataBaseSource baseSource;
    @Override
    public Object invoke()
            throws Throwable {
		AutopersQuery query = (AutopersQuery) args[0];
		AutopersObject autoPersObject = query.getAutopersObject();
		Map<String,Class<?>> xmlClassMap= autoPersObject._GetMappingClass();
		Map<String,AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();

		if(!autoPersObject._IsCombine()){
			Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
			AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
			if (mappingClass != null) {
				boolean isPark = AutopersPartFactory.isPart(autoPersObject);
				int count=0;
				if(isPark){
					Class parkClass = AutopersPartFactory.get(autoPersObject);
					AutopersPart part = (AutopersPart) parkClass.getConstructor().newInstance();
					List<String> tables = AutopersPartTool.getPartTables(baseSource, mappingClass.getName(), part, query);
					if(tables!=null&&tables.size()>0){
						PreparedStatement preparedStatement =persSession.getPreparedStatement();
						for (String table : tables) {
							StringBuffer queryMethodBuffer=AutopersQueryTool.getQuerySQL(query,platform,persSession,mappingClass,false);
							String sql="delete from "+ table+" "+autoPersObject._GetProxyClass().getSimpleName();
							logger.info(persSession.getIndex()+" "+sql);
							if(queryMethodBuffer!=null&&queryMethodBuffer.length()>0){
								sql+=" where "+queryMethodBuffer.toString();
							}else{
								sql="truncate table "+mappingClass.getName();
							}
							throwablePrint=persSession.getIndex()+" "+sql;
							count+=preparedStatement.executeUpdate(sql);
						}
						preparedStatement.close();
					}

				}else{

					StringBuffer queryMethodBuffer=AutopersQueryTool.getQuerySQL(query,platform,persSession,mappingClass,false);
					String sql="delete from "+ mappingClass.getName()+" "+autoPersObject._GetProxyClass().getSimpleName();
					logger.info(persSession.getIndex()+" "+sql);
					if(queryMethodBuffer!=null&&queryMethodBuffer.length()>0){
						sql+=" where "+queryMethodBuffer.toString();
					}else{
						sql="truncate table "+mappingClass.getName();
					}

					throwablePrint=persSession.getIndex()+" "+sql;
					PreparedStatement preparedStatement =persSession.getPreparedStatement();
					count=preparedStatement.executeUpdate(sql);
					preparedStatement.close();
				}

				return count;
			}
		}else{

		}

		return 0;
    }

	private String throwablePrint;

	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return throwablePrint;
	}

}
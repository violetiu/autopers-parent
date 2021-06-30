package org.violetime.autopers.session.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.violetime.autopers.database.DataBaseTable;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.session.AutopersSession;

public class GetTables implements AutopersPlatformInvoke{
	private AutopersSession session;

	private AutopersPlatformObject platformObject;

	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		PreparedStatement  preparedStatement=null;
		try{
			preparedStatement= session.getPreparedStatement();
			String sql="show table  status";
			if(platformObject==null)
				throw new Exception("platformObject is null");
			if(platformObject.getPropertys().get("sql")!=null)
				sql=platformObject.getPropertys().get("sql");
			ResultSet resultSet= preparedStatement.executeQuery(sql);
			List<DataBaseTable> baseTables=new ArrayList<DataBaseTable>();
			while(resultSet.next()){
				String tableName=resultSet.getString("name");
				if(platformObject.getPropertys().get("tableName")!=null)
					tableName=resultSet.getString(platformObject.getPropertys().get("tableName"));
				String updateTime=resultSet.getString(platformObject.getPropertys().get("updateTime"));
				String comment=resultSet.getString(platformObject.getPropertys().get("comment"));
				DataBaseTable baseTable=new DataBaseTable();
				baseTable.setTableName(tableName);
				baseTable.setUpdateTime(updateTime);
				baseTable.setComment(comment);
				baseTables.add(baseTable);
			}
			session.closePreparedStatement(preparedStatement);
			return baseTables;
			
		}catch(Exception  e){
			e.printStackTrace();
			if(preparedStatement!=null)
			session.closePreparedStatement(preparedStatement);
			return null;
		}
	}

	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}

}

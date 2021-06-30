package org.violetime.autopers.session.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.violetime.autopers.database.DataBaseColumn;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;

public class GetColumns implements AutopersPlatformInvoke{
	private AutopersSession session;
	private Object[] args;

	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				PreparedStatement  preparedStatement=null;
				String tableName=(String) args[0];
				try{
					String sql="select c.column_name colnumName,c.data_type dataType ,c.column_key columnkey,column_comment comment"
							+ " from  information_schema.columns  c where c.table_name='"+tableName+"'";
					preparedStatement= session.getPreparedStatement();
					ResultSet resultSet= preparedStatement.executeQuery(sql);
					List<DataBaseColumn> baseColnums=new ArrayList<DataBaseColumn>();
					HashMap<String, String> columnMap=new HashMap<>();
					while(resultSet.next()){
						String colnumName=resultSet.getString("colnumName");
						String dataType=resultSet.getString("dataType");
						String columnkey=resultSet.getString("columnkey");
						String comment=resultSet.getString("comment");
						DataBaseColumn baseColnum=new DataBaseColumn();
						baseColnum.setColnumName(colnumName);
						baseColnum.setJdbcType(dataType);
						baseColnum.setComment(comment);
						if(columnkey!=null&&columnkey.equals("PRI"))
							baseColnum.setPrimary("PRI");
						getColumnKeys(tableName, baseColnum);
						if(columnMap.get(colnumName)==null){
							columnMap.put(colnumName, colnumName);
							baseColnums.add(baseColnum);
						}
						
					}
					preparedStatement.close();
					return baseColnums;
					
				}catch(Exception  e){
					e.printStackTrace();
					try {
						preparedStatement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return null;
				}
	}
	public void getColumnKeys(String tableName,DataBaseColumn baseColnum){
		PreparedStatement  preparedStatement=null;
		try{
			String sql="SELECT   CONSTRAINT_NAME refName, REFERENCED_TABLE_NAME refTable ,REFERENCED_COLUMN_NAME refColumn "
					+ " FROM  information_schema.KEY_COLUMN_USAGE    "
					+ " WHERE   TABLE_NAME = '"+tableName+"' AND column_name ='"+baseColnum.getColnumName()+"'";
			preparedStatement= session.getPreparedStatement();
			ResultSet resultSet= preparedStatement.executeQuery(sql);
			while(resultSet.next()){
				String refName=resultSet.getString("refName");
				String refTable=resultSet.getString("refTable");
				String refColumn=resultSet.getString("refColumn");
				baseColnum.setRefName(refName);
				baseColnum.setRefTable(refTable);
				baseColnum.setRefColumn(refColumn);
			}
			preparedStatement.close();
			
		}catch(Exception  e){
			e.printStackTrace();
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}

}

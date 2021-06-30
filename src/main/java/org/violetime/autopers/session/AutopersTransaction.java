package org.violetime.autopers.session;

import java.sql.Connection;
import java.sql.SQLException;

public class AutopersTransaction {

	private Connection connection;
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void commit() throws SQLException{
		connection.commit();
	
	}
	public void rollback(){
		try {
			connection.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

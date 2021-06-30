package org.violetime.autopers.session.objects;

import java.sql.Connection;

import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionFactory;
import org.violetime.autopers.session.AutopersSessionPool;
import org.violetime.autopers.session.AutopersSessionState;

public class CloseSession implements AutopersPlatformInvoke{
	private AutopersSession persSession;
    private Connection connection;
    private DataBaseSource baseSource;
    @Override
    public Object invoke()
            throws Throwable {          
    	//System.out.println("CloseSession "+baseSource.getName());
		if( connection.getAutoCommit()){
			AutopersSessionFactory.recoverAutopersSession(persSession,baseSource.getName());
			
		}else{
			AutopersSessionState.ClOSE++;
			connection.close();
		}
        return null;
    }

	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
    
}
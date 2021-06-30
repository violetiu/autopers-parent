package org.violetime.autopers.session.objects;

import java.sql.Connection;

import org.violetime.autopers.platform.AutopersPlatformInvoke;
public class GetConnection implements AutopersPlatformInvoke{

    private  Connection connection;
    @Override
    public Object invoke()
            throws Throwable {
        return connection;
    }
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}

    
}
package org.violetime.autopers.session.objects;

import java.sql.CallableStatement;
import java.sql.Connection;

import org.violetime.autopers.platform.AutopersPlatformInvoke;

public class GetCallableStatement implements AutopersPlatformInvoke{
    private Connection connection;
    @Override
    public Object invoke()
            throws Throwable {
                CallableStatement callableStatement=connection.prepareCall("");
                return callableStatement;
    }
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
}
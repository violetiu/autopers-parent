package org.violetime.autopers.session.objects;

import java.sql.CallableStatement;

import org.violetime.autopers.platform.AutopersPlatformInvoke;


public class CloseCallableStatement implements AutopersPlatformInvoke{
    private Object[] args;
    @Override
    public Object invoke()
            throws Throwable {
             
                CallableStatement callableStatement=(CallableStatement)args[0];
                if(callableStatement!=null)
                callableStatement.close();
                return null;
    }
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
}
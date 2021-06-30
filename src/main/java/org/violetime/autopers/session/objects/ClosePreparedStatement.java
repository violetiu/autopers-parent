package org.violetime.autopers.session.objects;


import java.sql.PreparedStatement;

import org.violetime.autopers.platform.AutopersPlatformInvoke;
public class ClosePreparedStatement implements AutopersPlatformInvoke{
    private Object[] args;
    @Override
    public Object invoke()
            throws Throwable {
                PreparedStatement preparedStatement=(PreparedStatement)args[0];
                if(preparedStatement!=null)
                     preparedStatement.close();
                return null;
    }

	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
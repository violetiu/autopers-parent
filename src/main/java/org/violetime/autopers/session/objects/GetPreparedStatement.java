package org.violetime.autopers.session.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.violetime.autopers.platform.AutopersPlatformInvoke;

public class GetPreparedStatement implements AutopersPlatformInvoke{
    private  Connection connection;
    private Object[] args;
    @Override
    public Object invoke()
            throws Throwable {
                if(args!=null&&args.length==1){
                    PreparedStatement preparedStatement=connection.prepareStatement((String)args[0]);
                    return preparedStatement;
                }else{
                    PreparedStatement preparedStatement=connection.prepareStatement("select ");
                    return preparedStatement;
                }


    }
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
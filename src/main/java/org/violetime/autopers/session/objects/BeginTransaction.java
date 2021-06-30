package org.violetime.autopers.session.objects;

import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersTransaction;

import java.sql.Connection;

public class BeginTransaction  implements AutopersPlatformInvoke{

    private  Connection connection;
    @Override
    public Object invoke()
            throws Throwable {
                if (connection == null) {
                    // TODO
                    return null;
                }
                connection.setAutoCommit(false);
                AutopersTransaction autoPersTransaction = new AutopersTransaction();
                autoPersTransaction.setConnection(connection);
                return autoPersTransaction;
    }
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}

}
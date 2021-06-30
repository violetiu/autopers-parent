package org.violetime.autopers.session.objects;

import java.util.List;

import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;

public class SaveObject implements AutopersPlatformInvoke {
	private AutopersSession session;
	private Object[] args;
	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		 AutopersObject object=(AutopersObject) args[0];
		List<AutopersObject> list = session.queryObject(object);
		if (list == null || list.size() == 0) {
			return session.insertObject(object);
		} else {
			return session.updateObject(object);
		}
	}
	


	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}

	

}

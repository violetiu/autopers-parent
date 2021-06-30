package org.violetime.autopers.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class AutopersQueryFactory {

	/**
	 * 
	 * @return
	 */
	public static AutopersQuery newAutopersQueryObject(){
		InvocationHandler queryObjectProxy =new AutopersQueryProxy();
		AutopersQuery queryObject=(AutopersQuery) Proxy.newProxyInstance(queryObjectProxy.getClass().getClassLoader(), new Class[]{AutopersQuery.class},queryObjectProxy);
		return queryObject;
	}
	
	
}

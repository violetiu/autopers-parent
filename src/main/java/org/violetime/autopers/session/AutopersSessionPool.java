package org.violetime.autopers.session;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import java.util.logging.Logger;

import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;

/**
 * 连接池
 */
public class AutopersSessionPool {
	private final static Logger logger=Logger.getLogger("Autopers");
	private static HashMap<String, ArrayBlockingQueue<AutopersSession>> sessionPool = new HashMap<String, ArrayBlockingQueue<AutopersSession>>();
	private static String isLock=null;
	public static boolean isProjectModel=false;
	private static HashMap<String,AutopersSession> usingMap=new LinkedHashMap<>();
	public static HashMap<String,AutopersSession> getUsingMap(){
		return usingMap;
	}
	public  static void putUse(AutopersSession  session){
		session.useTime(System.currentTimeMillis());
		usingMap.put(session.getIndex()+"",session);
	}
	public static void freeUse(AutopersSession session){
		session.useTime(System.currentTimeMillis());
		usingMap.remove(session.getIndex()+"");
	}

	/**
	 * 获取连接池的大小
	 * @return
	 */
	public static int getPoolSize(){
		if(sessionPool==null)
			return 0;
		int count=0;
		for(Object key :sessionPool.keySet()){
			ArrayBlockingQueue<AutopersSession> autoPersSessions=sessionPool.get(key.toString());
			if(autoPersSessions!=null){
				count+=autoPersSessions.size();
			}
		}
		return count;
	}

	/**
	 * 上锁，如果已被其他线程锁住，返回null,
	 * @return
	 */
	public static synchronized String lock() {
		String key=null;
		if(isLock==null) {
			key=UUID.randomUUID().toString();
			isLock=key;
		}
		return key;
	}

	/**
	 * 释放锁
	 * @param key
	 */
	public static synchronized  void unLock(String key) {
		if(isLock!=null&&isLock.equals(key)) {
			isLock=null;
		}
	}
	public static synchronized  AutopersSession getSession(DataBaseSource baseSource,String key) {
		if(isLock==null||key==null||!isLock.equals(key)) 
			return null;
		AutopersSession session=null;
	    ArrayBlockingQueue<AutopersSession> sessionQueue=sessionPool.get(baseSource.getName());
	    if(sessionQueue==null) {
	    	sessionQueue=new ArrayBlockingQueue<AutopersSession>(baseSource.getSessionPoolCapacity());
	    }
	    if(sessionQueue.size()==0) {
	    	session =AutopersSessionFactory.createAutopersSession(baseSource);
	    	sessionPool.put(baseSource.getName(), sessionQueue);
	    }else {
	    	session=sessionQueue.poll();
	    	try {
				if(session.getConnection()==null||session.getConnection().isClosed()) {
					session =AutopersSessionFactory.createAutopersSession(baseSource);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		return session;
	}
	public static void  init() {
		logger.info("Initialize database connection pool");
		for(Object key: DataBaseFactory.getDataBaseSourceMap().keySet()) {
			DataBaseSource baseSource=DataBaseFactory.getDataBaseSourceMap().get(key);
			ArrayBlockingQueue<AutopersSession> queue=new ArrayBlockingQueue<AutopersSession>(baseSource.getSessionPoolCapacity());
			for(int index=0;index<baseSource.getInitialSize();index++) {
				AutopersSession	session =AutopersSessionFactory.createAutopersSession(baseSource);
				try {
					queue.put(session);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						session.getConnection().close();
						AutopersSessionState.ClOSE++;
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			sessionPool.put(key.toString(), queue);
			logger.info("Initialization of database connection pool completed, a total of completed:"+queue.size());
			
		}

		
		
	}
	public static synchronized boolean putSession(AutopersSession autoPersSession,DataBaseSource baseSource,String key) {
		if(isLock==null||key==null||!isLock.equals(key)) 
			return false;
		 ArrayBlockingQueue<AutopersSession> sessionQueue=sessionPool.get(baseSource.getName());
		    if(sessionQueue==null) {
		    	sessionQueue=new ArrayBlockingQueue<AutopersSession>(baseSource.getSessionPoolCapacity());
		    	sessionPool.put(baseSource.getName(), sessionQueue);
		    }
		    try {
				autoPersSession.codeId(null);
	    		sessionQueue.put(autoPersSession);
	    	}catch (Exception e) {
				// TODO: handle exception
	    		e.printStackTrace();
	    		try {
	    			autoPersSession.getConnection().close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		return false;
			}
		return true;
	}
	public static HashMap<String, ArrayBlockingQueue<AutopersSession>> getSessionPool(String key) {
		if(isLock==null||key==null||!isLock.equals(key)) 
			return null;
		return sessionPool;
	}
	public static void setSessionPool(HashMap<String, ArrayBlockingQueue<AutopersSession>> sessionPool,String key) {
		if(isLock==null||key==null||!isLock.equals(key)) 
			return;
		AutopersSessionPool.sessionPool = sessionPool;
	}
	
 }

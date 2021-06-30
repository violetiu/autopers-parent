package org.violetime.autopers.session;

import org.violetime.autopers.cache.AutopersCache;
import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.platform.*;
import org.violetime.autopers.session.tool.AutopersQueryTool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * 会话代理类
 */
public class AutopersSessionProxy implements InvocationHandler {

    private Connection connection;
    private AutopersPlatform platform;
    private AutopersSession session;
    private Long lifeTime;
    private Long useTime;
    private DataBaseSource baseSource;
    private final static Logger logger = Logger.getLogger("Autopers");
    private String codeId;
    private long index = 0;

    public AutopersSessionProxy(Connection connection, DataBaseSource baseSource) {
        this.connection = connection;
        this.platform = AutopersPlatformFactory.getPlatform(baseSource);
        this.baseSource = baseSource;
        this.lifeTime = new Date().getTime();
    }

    public AutopersSessionProxy(Connection connection, DataBaseSource baseSource, long index) {
        this.connection = connection;
        this.platform = AutopersPlatformFactory.getPlatform(baseSource);
        this.baseSource = baseSource;
        this.lifeTime = new Date().getTime();
        this.index = index;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (session == null) {
            session = (AutopersSession) proxy;
        }
        if (method.getName().equals("lifeTime") && method.getParameterCount() == 0) {
            return (new Date().getTime() - lifeTime) / 60000;
        } else if (method.getName().equals("lifeTime") && method.getParameterCount() == 1) {
            lifeTime = (Long) args[0];
            return null;
        }

        if (method.getName().equals("useTime") && method.getParameterCount() == 0) {
            return (new Date().getTime() - useTime) / 60000;
        } else if (method.getName().equals("useTime") && method.getParameterCount() == 1) {
            useTime = (Long) args[0];
            return null;
        }

        if (method.getName().equals("getIndex") && method.getParameterCount() == 0) {
            return index;
        }
        if (method.getName().equals("codeId") && method.getParameterCount() == 0) {
            return codeId;
        } else if (method.getName().equals("codeId") && method.getParameterCount() == 1) {
            codeId = (String) args[0];
            return null;
        } else if (method.getName().equals("copySession")) {
            AutopersSession copy = AutopersSessionFactory.openSession(baseSource);
            return copy;
        }


        if (platform == null) {
            throw new Exception(baseSource.getName() + " platform is null!");
        }
        AutopersPlatformPackage platformPackage = platform.getPackageMap().get(AutopersSession.class.getName());
        HashMap<String, AutopersPlatformObject> objectMap = platformPackage.getObjectMap();
        AutopersPlatformObject platformObject = objectMap.get(method.getName());
        if (platformObject == null) {
            throw new Exception("AutopersPlatformObject is null ,method.name->" + method.getName());
        }
        String objectClass = platformObject.getClassName();
        if (codeId == null || codeId.length() == 0)
            codeId = AutopersQueryTool.getCodeIdFromObjectStatic(7);

        int count = 0;
        int maxCount = 2;
        while (count < maxCount) {
            AutopersPlatformInvoke platformInvoke = null;
            if (AutopersSessionPool.isProjectModel) {
                Object cache = AutopersCache.poll(codeId);
                if (cache != null) {
                    platformInvoke = (AutopersPlatformInvoke) cache;
                    setPlatformArgs(objectClass, platformInvoke, args);
                } else {
                    platformInvoke = getPlatformInvoke(objectClass, platformObject, method, args, proxy);
                }
            } else {
                platformInvoke = getPlatformInvoke(objectClass, platformObject, method, args, proxy);
            }
            if (platformInvoke != null) {
                try {
                    //执行数据操作,并返回
                    Object result = platformInvoke.invoke();
                    if (AutopersSessionPool.isProjectModel) {
                        AutopersCache.push(codeId, platformInvoke);
                    }
                    return result;
                } catch (Throwable e) {
                    String print = platformInvoke.throwablePrint();
                    if (print != null)
                        logger.info(print);
                    if (e.getMessage().contains("Communications link failure")) {
                        //如果 因为时间 连接失效，重新获取连接。
                        Connection newConnection = DataBaseFactory.getConnection(baseSource);
                        connection = newConnection;
                    } else {
                        // TODO: handle exception
                        throw e;
                    }
                } finally {
                    count++;
                }

            } else {
                logger.info("platformInvoke is null!");
            }
        }
        return null;
    }

    private void setField(Class<?> invokeClass, AutopersPlatformInvoke platformInvoke, Field field, Object object) throws Exception {
        field.setAccessible(true);
        field.set(platformInvoke, object);
        field.setAccessible(false);
    }

    private void setPlatformArgs(String objectClass, AutopersPlatformInvoke platformInvoke, Object[] args) throws Exception {
        Class<?> invokeClass = Class.forName(objectClass);
        //设置属性值
        for (Field field : invokeClass.getDeclaredFields()) {


            if (field.getType().equals(Object[].class)) {
                setField(invokeClass, platformInvoke, field, args);
                continue;

            }

        }

    }

    private AutopersPlatformInvoke getPlatformInvoke(String objectClass, AutopersPlatformObject platformObject, Method method, Object[] args, Object proxy) throws Exception {
        Class<?> invokeClass = Class.forName(objectClass);
        AutopersPlatformInvoke platformInvoke = (AutopersPlatformInvoke) invokeClass.getConstructor().newInstance(null);


        //设置属性值
        for (Field field : invokeClass.getDeclaredFields()) {

            if (field.getType().equals(Connection.class)) {

                setField(invokeClass, platformInvoke, field, connection);
                continue;
            }
            if (field.getType().equals(AutopersPlatformObject.class)) {
                setField(invokeClass, platformInvoke, field, platformObject);
                continue;
            }
            if (field.getType().equals(AutopersPlatform.class)) {
                setField(invokeClass, platformInvoke, field, platform);
                continue;
            }
            if (field.getType().equals(DataBaseSource.class)) {
                setField(invokeClass, platformInvoke, field, baseSource);
                continue;
            }
            if (field.getType().equals(AutopersSession.class)) {
                setField(invokeClass, platformInvoke, field, session);
                continue;

            }
            if (field.getType().equals(Method.class)) {
                setField(invokeClass, platformInvoke, field, method);
                continue;
            }
            if (field.getType().equals(Object[].class)) {
                setField(invokeClass, platformInvoke, field, args);
                continue;

            }
            if (field.getType().equals(proxy.getClass())) {
                setField(invokeClass, platformInvoke, field, proxy);
                continue;

            }
        }
        return platformInvoke;
    }

}
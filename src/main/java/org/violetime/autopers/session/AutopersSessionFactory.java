package org.violetime.autopers.session;

import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;



/**
 * 数据库会话工厂
 *
 * @author taoyo
 */
public class AutopersSessionFactory {
    private final static Logger logger = Logger.getLogger("Autopers");

    /**
     * 打开一个数据库会话
     *
     * @return AutopersSession
     */
    public static AutopersSession openSession() {
        DataBaseSource baseSource = DataBaseFactory.getDataBaseSource();
        return openSession(baseSource);

    }

    /**
     * 打开一个数据库会话
     *
     * @param databaseName DataBaseSource的name,即配置的数据库连接名称
     * @return AutopersSession
     */
    public static AutopersSession openSession(String databaseName) {
        DataBaseSource baseSource = DataBaseFactory.getDataBaseSource(databaseName);
        return openSession(baseSource);
    }

    /**
     * 打开一个数据库会话
     *
     * @param baseSource DataBaseSource
     * @return AutopersSession
     */
    public static AutopersSession openSession(DataBaseSource baseSource) {
        AutopersSession autoPersSession = null;
        while (true) {
            String key = AutopersSessionPool.lock();
            if (key == null)
                continue;
            autoPersSession = AutopersSessionPool.getSession(baseSource, key);
            AutopersSessionPool.unLock(key);
            if (autoPersSession != null) {

                break;
            }
        }
        return autoPersSession;
    }

    /**
     * 创建session
     *
     * @param baseSource
     * @return
     */
    public static AutopersSession createAutopersSession(DataBaseSource baseSource) {
        Connection connection = DataBaseFactory.getConnection(baseSource);
        InvocationHandler sessionProxy = new AutopersSessionProxy(connection, baseSource, AutopersSessionState.INIT + 1);
        AutopersSession autoPersSession =
                (AutopersSession) Proxy.newProxyInstance(
                        sessionProxy.getClass().getClassLoader(), new Class[]{
                                AutopersSession.class}, sessionProxy);
        AutopersSessionState.INIT++;
        return autoPersSession;

    }

    /**
     * 回收session
     *
     * @param persSession
     * @param baseSourceName
     * @throws SQLException
     */
    public static void recoverAutopersSession(AutopersSession persSession, String baseSourceName) throws SQLException {
        AutopersSessionPool.freeUse(persSession);
        DataBaseSource baseSource = DataBaseFactory.getDataBaseSource(baseSourceName);
        if (baseSource == null) {
            persSession.getConnection().close();
            AutopersSessionState.ClOSE++;
            return;
        }

        int count = 0;
        while (true) {
            count++;
            if (count > 100) {
                persSession.getConnection().close();
                AutopersSessionState.ClOSE++;
                break;
            }
            String key = AutopersSessionPool.lock();
            if (key == null) {
                try {
                    Thread.sleep(50l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Boolean rs = AutopersSessionPool.putSession(persSession, baseSource, key);
            AutopersSessionPool.unLock(key);
            if (rs)
                break;
            try {
                Thread.sleep(50l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

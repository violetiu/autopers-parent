package org.violetime.autopers.session;

/**
 * 连接池状态统计
 */
public class AutopersSessionState {
    public static long INIT=0;
    public static long ClOSE=0;

    /**
     * 获取已创建的连接数
     * @return
     */
    public static long getInit() {
        return INIT;
    }

    /**
     * 获取已关闭的连接数
     * @return
     */
    public static long getClose() {
        return ClOSE;
    }

    /**
     * 获取连接池中连接个数
     * @return
     */
    public static long getPool() {
        return AutopersSessionPool.getPoolSize();
    }
}

package org.violetime.autopers.session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;

/**
 * 连接池管理线程，控制连接池的大小与连接生命周期控制
 */
public class AutopersSessionPoolThread extends Thread {
    public static void main(String[] args) {
        new AutopersSessionPoolThread().start();

    }

    private Timer timer;

    @Override
    public void interrupt() {
        super.interrupt();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void run() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //处理长时间使用的连接
                try {
                    HashMap<String, AutopersSession> usingMap = AutopersSessionPool.getUsingMap();
                    List<String> deletes = null;
                    for (String index : usingMap.keySet()) {
                        AutopersSession session = usingMap.get(index);
                        if (session.useTime() > 1000 * 60 * 5) {
                            //如果连接使用超过5分钟，直接关闭连接
                            try {
                                session.getConnection().close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (deletes == null) deletes = new ArrayList<>();
                            deletes.add(session.getIndex() + "");
                        }
                    }
                    if (deletes != null) {
                        for (String delete : deletes) {
                            usingMap.remove(usingMap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //处理创建时间过长的连接
                String key = AutopersSessionPool.lock();
                if (key == null)
                    return;
                try {

                    HashMap<String, ArrayBlockingQueue<AutopersSession>> sessionPool = AutopersSessionPool.getSessionPool(key);
                    for (Object keyS : sessionPool.keySet()) {
                        ArrayBlockingQueue<AutopersSession> queue = sessionPool.get(keyS);
                        DataBaseSource baseSource = DataBaseFactory.getDataBaseSource(keyS.toString());
                        if (queue == null)
                            continue;
                        while (queue.size() > 0) {
                            AutopersSession autoPersSession = queue.peek();
                            if (autoPersSession == null)
                                break;
                            if (autoPersSession.lifeTime() > baseSource.getSessionMaxLifeTime()) {
                                AutopersSessionState.ClOSE++;
                                queue.remove();
                                try {
                                    autoPersSession.getConnection().close();
                                } catch (Exception e) {
                                }
                            } else {
                                break;
                            }
                        }
                        if (queue.size() < baseSource.getSessionPoolMinSize()) {
                            while (queue.size() < baseSource.getSessionPoolMinSize()) {
                                AutopersSession persSession = AutopersSessionFactory.createAutopersSession(baseSource);
                                try {
                                    queue.put(persSession);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    try {
                                        AutopersSessionState.ClOSE++;
                                        persSession.getConnection().close();
                                    } catch (SQLException e1) {
                                        // TODO Auto-generated catch block
                                        // e1.printStackTrace();
                                    }
                                }
                            }
                        } else if (queue.size() > baseSource.getSessionPoolCapacity()) {
                            while (queue.size() > baseSource.getSessionPoolCapacity()) {
                                AutopersSession persSession = queue.poll();
                                if (persSession != null)
                                    try {
                                        AutopersSessionState.ClOSE++;
                                        persSession.getConnection().close();
                                    } catch (SQLException e) {
                                        // TODO Auto-generated catch block
                                        // e.printStackTrace();
                                    }
                            }
                        }
                        sessionPool.put(keyS.toString(), queue);
                    }
                    AutopersSessionPool.unLock(key);
                } catch (Exception e) {
                    // TODO: handle exception
                    AutopersSessionPool.unLock(key);
                    e.printStackTrace();
                }
            }
        }, 10000, 10000);

    }

}

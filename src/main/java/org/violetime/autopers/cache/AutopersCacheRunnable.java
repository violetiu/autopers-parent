package org.violetime.autopers.cache;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 缓存数据管理线程
 */
public class AutopersCacheRunnable extends Thread {
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
                // TODO Auto-generated method stub
                HashMap<String, ArrayBlockingQueue<AutopersCacheBlock>> queueHashMap = AutopersCache.getCacheQueueMap();
                if (queueHashMap != null && queueHashMap.size() > 0) {
                    for (Object key : queueHashMap.keySet()) {
                        ArrayBlockingQueue<AutopersCacheBlock> cacheBlocks = queueHashMap.get(key);
                        if (cacheBlocks != null && cacheBlocks.size() > 0) {
                            cacheBlocks.removeIf((bock) -> (bock.getAgeMs() > 1000 * 60 * 60));
                        }
                    }
                }
            }
        }, 10000, 60000);
    }
}


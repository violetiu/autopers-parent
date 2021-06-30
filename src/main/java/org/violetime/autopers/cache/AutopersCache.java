package org.violetime.autopers.cache;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 缓存数据
 */
public class AutopersCache{
    private static HashMap<String, ArrayBlockingQueue<AutopersCacheBlock>> cacheQueueMap = new HashMap<String, ArrayBlockingQueue<AutopersCacheBlock>>();

    public static HashMap<String, ArrayBlockingQueue<AutopersCacheBlock>> getCacheQueueMap() {
        return cacheQueueMap;
    }

    /**
     *存入一个缓存块
     * @param key
     * @param object
     */
    public  static synchronized void push(String key,Object object){
        ArrayBlockingQueue<AutopersCacheBlock> cacheBlocks=cacheQueueMap.get(key);
        if(cacheBlocks==null){
            cacheBlocks=new ArrayBlockingQueue<AutopersCacheBlock>(10);
        }
        AutopersCacheBlock cacheBlock=new AutopersCacheBlock();
        cacheBlock.setObject(object);
        cacheBlocks.add(cacheBlock);
    }

    /**
     * 提取一个缓存块
     * @param key
     * @return
     */
    public  static synchronized Object poll(String key){
        ArrayBlockingQueue<AutopersCacheBlock> cacheBlocks=cacheQueueMap.get(key);
        if(cacheBlocks==null){
           return  null;
        }
        AutopersCacheBlock cacheBlock=cacheBlocks.poll();
        if(cacheBlock==null)
            return null;
        return cacheBlock.getObject();
    }
    /**
     * 提取一个缓存块
     * @param key
     * @return
     */
    public  static synchronized Object peek(String key){
        ArrayBlockingQueue<AutopersCacheBlock> cacheBlocks=cacheQueueMap.get(key);
        if(cacheBlocks==null){
            return  null;
        }
        AutopersCacheBlock cacheBlock=cacheBlocks.peek();
        if(cacheBlock==null)
            return null;
        return cacheBlock.getObject();
    }
}

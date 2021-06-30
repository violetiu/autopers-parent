package org.violetime.autopers.cache;

/**
 * 缓存数据块
 */
public class AutopersCacheBlock {
    private long date;
    private Object object;
    public AutopersCacheBlock() {
        this.date=System.currentTimeMillis();
    }
    public Object getObject() {
        return object;
    }
    public void setObject(Object object) {
        this.object = object;
    }

    public long getAgeMs() {
        return  System.currentTimeMillis()-this.date;
    }

}

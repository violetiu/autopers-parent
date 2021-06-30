package org.violetime.autopers.platform;
/**
 * 平台对象代理接口
 */
public interface AutopersPlatformInvoke{

    /**
     * 
     * 该对象的私有属性，会按照类型自动注入。支持的私有属性定义如下：
     *  Connection；AutopersPlatform；Method.；Object[]；和代理的其他类
     * 

     * @return Object
     * @throws Throwable
     */
    public Object invoke() throws Throwable;
    /**
     * 
     * 
     */
    public String throwablePrint();
    

}
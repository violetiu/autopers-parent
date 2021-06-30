package org.violetime.autopers.function;
/**
 * 聚合函数定义
 */
public interface AutopersFunctionGroup extends  AutopersFunctionSuper {
    /**
     * 最大值
     */
    public  AutopersFunction _Max();
    /**
     * 最小值
     */
    public  AutopersFunction _Min();
    /**
     * 求和
     */
    public  AutopersFunction _Sum();
    /**
     * 平均
     */
    public  AutopersFunction _Avg();
    /**
     * 计数
     */
    public  AutopersFunction _Count();
    /**
     * 标准差
     */
    public AutopersFunction _Stdev();
    /**
     * 方差
     */
    public AutopersFunction _Var();
    /**
     * 去重复
     */
    public AutopersFunction _Distinct();
    /**
     * --start起始位置（从1开始）
     * @param start
     * @param length
     * @return
     */
    public AutopersFunction _SubString(int start,int length);


}

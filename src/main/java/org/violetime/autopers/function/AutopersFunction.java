package org.violetime.autopers.function;

import java.util.function.Function;

/**
 * 聚合函数定义
 */
public interface AutopersFunction extends AutopersFunctionGroup,AutopersFunctionSingle {
    /**
     * 取值
     */
    public void  _None();

    /**
     * 函数
     */
    public void  _Fun(Function  function);

}

package org.violetime.autopers.function;
/**
 * 函数定义
 */
public interface AutopersFunctionSingle extends  AutopersFunctionSuper{

    /**
     * 计算字符串长度
     */
    public AutopersFunction _Length();

    /**
     * --start起始位置（从1开始）
     * @param start
     * @param length
     * @return
     */
    public AutopersFunction _SubString(int start,int length);

    /**
     * 求绝对值
     * @return
     */
    public AutopersFunction _Abs();
    /**
     * 取大于等于指定值的最小整数
     * @return
     */
    public AutopersFunction _Ceiling();

    /**
     * 取指数
     * @return
     */
    public AutopersFunction _Exp();


    /**
     * 小于等于指定值得最大整数
     * @return
     */
    public AutopersFunction _Floor();



    /**
     *  返回power次方
     * @return
     */
    public AutopersFunction _Power(double power);

    /**
     *  安int_expr规定的精度四舍五入
     * @return
     */
    public AutopersFunction _Round();
    /**
     *  根据正数,0,负数,,返回+1,0,-1
     * @return
     */
    public AutopersFunction _Sign();
    /**
     * 平方根
     * @return
     */
    public AutopersFunction _Sqrt();

    /**

     */
}

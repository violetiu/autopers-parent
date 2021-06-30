package org.violetime.autopers.units;


import java.util.function.Function;

/**
 * 普通数字
 */
public class Numbers {

    /**
     * 四舍五入
     * @param number
     * @param precision
     * @return
     */
    public static  double round(double number,int precision){
        double value=Math.round(number*Math.pow(10,precision))/Math.pow(10,precision);

        return value;
    }



}

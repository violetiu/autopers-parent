package org.violetime.autopers.units;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.function.AutopersFunctionSingle;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.ObjectFactory;
import org.violetime.autopers.session.AutopersSession;
import java.util.logging.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

public class AutopersTest {
    public static void main(String[] args) {

        test();

        AutopersObject autoPersObject=null;
        try{

            autoPersObject._Result(autoPersObject._Query())._Fun(i->i+"");
            autoPersObject._Result(autoPersObject._Query())._Length()._Max();
            autoPersObject._Query()._Equals_F(autoPersObject.getClass(),"")._Exp();
            autoPersObject._Save();
        }catch (Exception e){

        }




    }
    public static void demo(Function<Object,Boolean> gun){


    }
    private static void test(){
        Numbers.round(123.1231,2);
    }
}

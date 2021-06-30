package org.violetime.autopers.function;

import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.query.AutopersQueryProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class AutopersFunctionFactory {
    /**
     *
     * @return
     */
    public static AutopersFunction newAutopersFunction(){
        InvocationHandler functionProxy =new AutopersFunctionProxy();
        AutopersFunction function=(AutopersFunction) Proxy.newProxyInstance(functionProxy.getClass().getClassLoader(), new Class[]{AutopersFunction.class},functionProxy);
        return function;
    }
    public static AutopersFunction newAutopersFunction(  AutopersObjectField field){
        InvocationHandler functionProxy =new AutopersFunctionProxy(field);
        AutopersFunction function=(AutopersFunction) Proxy.newProxyInstance(functionProxy.getClass().getClassLoader(), new Class[]{AutopersFunction.class},functionProxy);
        return function;
    }


}

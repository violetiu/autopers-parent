package org.violetime.autopers.objects;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.violetime.autopers.cache.AutopersCache;
import org.violetime.autopers.objects.impl.AutopersObjectFieldDefault;
import org.violetime.autopers.objects.impl.AutopersObjectFieldProxy;
import org.violetime.autopers.session.AutopersSession;
/**
 * <p>用户创建表接口代理类及拥有的属性代理类
 * @author taoyongwen
 */
public  class AutopersObjectsFactory{
	/**
	 * 实例化一个简单数据库表对象
	 * @param object
	 * @return
	 */
	public static AutopersObject newInstanceSimpleObject(Class object){
		Class[] interfaces=new Class[object.getInterfaces().length+1];
		for( int index=0;index<object.getInterfaces().length;index++){
			interfaces[index]=object.getInterfaces()[index];
		}
		interfaces[object.getInterfaces().length]=object;
		AutopersObjectSimpleProxy objectProxy=new AutopersObjectSimpleProxy(object);
		AutopersObject persObject=(AutopersObject)Proxy.newProxyInstance(objectProxy.getClass().getClassLoader(),interfaces, objectProxy);
		return persObject;
	}
	/**
	 * 实例化一个数据库表对象
	 * @param object
	 * @return
	 */
	public static AutopersObject newInstanceObject(Class object){
		Class[] interfaces=new Class[object.getInterfaces().length+1];
		for( int index=0;index<object.getInterfaces().length;index++){
			interfaces[index]=object.getInterfaces()[index];
		}
		interfaces[object.getInterfaces().length]=object;
		AutopersObjectProxy objectProxy=new AutopersObjectProxy(object);
		AutopersObject persObject=(AutopersObject)Proxy.newProxyInstance(objectProxy.getClass().getClassLoader(),interfaces, objectProxy);	
		return persObject;
	}
	/**
	 * 实例化一个数据库表对象
	 * @param object
	 * @param session
	 * @return
	 */
	public static AutopersObject newInstanceObject(Class object,AutopersSession session){
		Class[] interfaces=new Class[object.getInterfaces().length+1];
		for( int index=0;index<object.getInterfaces().length;index++){
			interfaces[index]=object.getInterfaces()[index];
		}
		interfaces[object.getInterfaces().length]=object;
		AutopersObjectProxy objectProxy=new AutopersObjectProxy(object);
		objectProxy.setSession(session);
		AutopersObject persObject=(AutopersObject)Proxy.newProxyInstance(objectProxy.getClass().getClassLoader(),interfaces, objectProxy);
		return persObject;
	}

    /**
     * 实例化一个对象属性
     * @param fieldValue
     * @param fieldName
     * @param className
     * @return
     */
    public static AutopersObjectField newInstanceField(Object fieldValue,String fieldName,String className){
        try{
            InvocationHandler fieldProxy=new AutopersObjectFieldProxy();
            AutopersObjectField field=(AutopersObjectField)Proxy.newProxyInstance(fieldProxy.getClass().getClassLoader(), AutopersObjectFieldDefault.class.getInterfaces(), fieldProxy);
            field.setField(fieldName);
            field.setClassName(className);
            field.setValue(fieldValue);
            return field;
        }catch(Exception e){
            e.printStackTrace();
        }
      
     return null;   
    }
	/**
	 * 实例化一个对象属性
	 * @param fieldValue
	 * @param fieldName
	 * @param className
	 * @return
	 */
	public static AutopersObjectField newInstanceField(Object fieldValue,String fieldName,String className,String fieldClassName){
		try{
			InvocationHandler fieldProxy=new AutopersObjectFieldProxy();
			AutopersObjectField field=(AutopersObjectField)Proxy.newProxyInstance(fieldProxy.getClass().getClassLoader(), AutopersObjectFieldDefault.class.getInterfaces(), fieldProxy);
			field.setField(fieldName);
			field.setClassName(className);
			field.setValue(fieldValue);
			field.setResult(false);
			field.setFieldClassName(fieldClassName);
			return field;
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}
    /**
     * 实例化一个对象属性
     * @return
     */
    public static AutopersObjectField newInstanceField(){
        try{
            AutopersObjectFieldProxy fieldProxy=new AutopersObjectFieldProxy();
            AutopersObjectField field=(AutopersObjectField)Proxy.newProxyInstance(fieldProxy.getClass().getClassLoader(), AutopersObjectFieldDefault.class.getInterfaces(), fieldProxy);
            return field;
        }catch(Exception e){
            e.printStackTrace();
        }
      
     return null;   
    }
}
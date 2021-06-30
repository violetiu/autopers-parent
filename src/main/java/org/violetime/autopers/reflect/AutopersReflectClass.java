package org.violetime.autopers.reflect;
/**
 * 类反射 
 * @author taoyo
 *
 */
public class AutopersReflectClass   {


	public static boolean isImplements(Class child,Class parent){
		if(child.getInterfaces()==null||child.getInterfaces().length==0)
			return false;
		for(Class item : child.getInterfaces()){
			if(item.equals(parent)){
				return true;
			}
		}
		return  false;

	}

	/**
	 * 实例化
	 * @param className
	 * @return
	 */
	public static Object getObject(String className){
		Class cls;
		try {
			cls = Class.forName(className);
			Object object = cls.getConstructors()[0].newInstance(null);
			return object;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
}

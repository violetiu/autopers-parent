package org.violetime.autopers.reflect;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutopersReflectObject {
	private final static Logger logger=Logger.getLogger(AutopersReflectObject.class.getName());
	private Object object;
	private Class cls;
	
	public Object getObject() {
		return object;
	}
	public AutopersReflectObject(Object object){
		this.object=object;
		this.cls=object.getClass();
	}
	public Object getField(String field){
		String functionName="get"+field.substring(0,1).toUpperCase()+field.substring(1);
		Method method= getMethodByName(functionName);
		if(method!=null){
			try {
				return method.invoke(object);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public void setField(String field,Object value) throws Exception{

		Field fieldObject=null;
		try{
			fieldObject=cls.getDeclaredField(field);
		}catch (NoSuchFieldException e){
			logger.log(Level.FINE,e.getMessage());
		}
		if(fieldObject==null)
			return;
		if(fieldObject.getType().equals(value.getClass())){
			fieldObject.setAccessible(true);
			fieldObject.set(object,value);
		}else if(value.getClass().equals(String.class)){
			if(fieldObject.getType().equals(Integer.class)||fieldObject.getType().equals(int.class)){
				fieldObject.setAccessible(true);
				fieldObject.set(object,Integer.parseInt(value.toString()));
			}else if(fieldObject.getType().equals(Double.class)||fieldObject.getType().equals(double.class)){
				fieldObject.setAccessible(true);
				fieldObject.set(object, Double.parseDouble(value.toString()));
			}
			else if(fieldObject.getType().equals(Long.class)||fieldObject.getType().equals(long.class)){
				fieldObject.setAccessible(true);
				fieldObject.set(object, Long.parseLong(value.toString()));
			}
			else if(fieldObject.getType().equals(Date.class)){
				if(value.toString().matches("[0-9]{4}\\D[0-1]{1}[0-9]{1}\\D[0-3]{1}[0-9]{1}")){
					String temp=value.toString().replaceAll("\\D","");
					SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
					fieldObject.setAccessible(true);
					fieldObject.set(object,simpleDateFormat.parse(temp));
				}else if(value.toString().matches("[0-2]{1}[0-9]{1}\\D[0-5]{1}[0-9]{1}\\D[0-5]{1}[0-9]{1}")){
					String temp=value.toString().replaceAll("\\D",":");
					SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm:ss");
					fieldObject.setAccessible(true);
					fieldObject.set(object,simpleDateFormat.parse(temp));
				}

			}
		}
		fieldObject.setAccessible(false);
		
	}
	private Method getMethodByName(String methodName){
		for(Method method:cls.getMethods()){
			if(method.getName().equals(methodName)){
				return  method;
			}
		}
		return null;
	}
}

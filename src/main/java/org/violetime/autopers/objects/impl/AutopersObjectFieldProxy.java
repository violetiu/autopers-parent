package org.violetime.autopers.objects.impl;
import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectsFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
public class AutopersObjectFieldProxy implements InvocationHandler{

	   private String fieldName;
	   private String className;
       private Object fieldValue;
       private String comment;
       private  String fieldClassName;
       private AutopersFunction queryFunction;
		private boolean isCombine=false;
		private  boolean  result;
		private String combineField;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


		if(method.getName().equals("isResult")){

			return this.result;
		}
		if(method.getName().equals("setResult")){
			this.result=(Boolean) args[0];
			return null;
		}
    	if(method.getName().equals("getClassName")){
    		
    		return className;
    	}
		if(method.getName().equals("getValue")){
			return fieldValue;
		    		
		 }
		if(method.getName().equals("setValue")){
			this.fieldValue=(Object) args[0];
			 return null;
		}
		if(method.getName().equals("getField")){
			return fieldName;
			
		}
		if(method.getName().equals("setField")){
			this.fieldName=(String) args[0];
			 return null;
		}
		if(method.getName().equals("getComment")){
			return comment ;

		}
		if(method.getName().equals("setComment")){
			this.comment=(String) args[0];
			return null;
		}
		if(method.getName().equals("setClassName")){
			this.className=(String) args[0];
			 return null;
		}

		if(method.getName().equals("setQueryFunction")){
			this.queryFunction=(AutopersFunction) args[0];
			return null;
		}
		if(method.getName().equals("getQueryFunction")){
			return this.queryFunction;
		}

		if(method.getName().equals("setCombine")){
			this.isCombine=(Boolean) args[0];
			return null;
		}
		if(method.getName().equals("isCombine")){
			return this.isCombine;
		}


		if(method.getName().equals("setCombineField")){
			this.combineField=(String) args[0];
			return null;
		}
		if(method.getName().equals("getCombineField")){
			return this.combineField;
		}
		if(method.getName().equals("setFieldClassName")){
			this.fieldClassName=(String) args[0];
			return null;
		}
		if(method.getName().equals("getFieldClassName")){
			return this.fieldClassName;
		}
		if(method.getName().equals("clone")){
			AutopersObjectField  objectField= AutopersObjectsFactory.newInstanceField(fieldValue,fieldName,className);

			return objectField;
		}

		return null;
    }


}
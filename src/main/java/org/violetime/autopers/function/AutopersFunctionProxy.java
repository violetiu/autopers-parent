package org.violetime.autopers.function;

import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.platform.AutopersPlatformPackage;
import org.violetime.autopers.query.AutopersQueryMethod;
import java.util.logging.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询语句代理类
 * @author taoyo
 *
 */
public class AutopersFunctionProxy implements InvocationHandler {
	private final static Logger logger=Logger.getLogger("Autopers");
	private Map<String, Object[]> functions;
	private AutopersObjectField field;

	public AutopersFunctionProxy(AutopersObjectField field) {
		this.field = field;
	}

	public AutopersFunctionProxy() {
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		if(field!=null&&field.getQueryFunction()==null)
		{
			field.setQueryFunction((AutopersFunction) proxy);
		}

		if(method.getName().equals("getFunctions")){
			return  functions;
		}else
		if(method.getName().equals("_None")){
			if(functions==null)
				functions=new LinkedHashMap<>();
			functions.put(method.getName(),args);
			return  null;
		}else if(method.getName().equals("_Fun")){
			if(functions==null)
				functions=new LinkedHashMap<>();
			functions.put(method.getName(),args);
			return  null;
		}else
		if(method.getName().equals("getSQl")){
			//TODO
			String column= (String) args[0];
			if(functions==null||functions.size()==0)
				return column;

			AutopersPlatform platform= (AutopersPlatform) args[1];

			AutopersPlatformPackage platformPackage= platform.getPackageMap().get("org.violetime.autopers.function");

			AutopersPlatformObject platformObject= platformPackage.getObjectMap().get("AutopersFunctionProxy");
			Map<String,String> propertys= platformObject.getPropertys();
			StringBuffer result=new StringBuffer();
			for(String functionName:functions.keySet()){
				if(functionName.equals("_None")){
					result.append(column);
					continue;
				}

				Object[] functionArgs=functions.get(functionName);
				String sqlTemplate=propertys.get(functionName);
				if(result.length()==0){
					sqlTemplate=sqlTemplate.replace("[column]",column);
				}else{
					sqlTemplate=sqlTemplate.replace("[column]",result.toString());
				}
				if(functionArgs!=null&&functionArgs.length>0){
					for(int index=0;index<functionArgs.length;index++){
						sqlTemplate=sqlTemplate.replace("["+index+"]", functionArgs[index].toString());
					}
				}
				result.append(sqlTemplate);
			}
			return  result.toString();
		}else
		if(method.getName().startsWith("_")){
			if(functions==null)
				functions=new LinkedHashMap<>();
			functions.put(method.getName(),args);
			return proxy;
		}else {
			return  null;
		}

	}

}

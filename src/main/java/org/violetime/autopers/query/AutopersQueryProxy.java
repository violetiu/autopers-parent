package org.violetime.autopers.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.function.AutopersFunctionFactory;

import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
/**
 * 查询语句代理类
 * @author taoyo
 *
 */
public class AutopersQueryProxy implements InvocationHandler {
	private final static Logger logger = Logger.getLogger("Autopers");
	private  List<AutopersQueryMethod> methods;
	private List<AutopersObjectField> queryFieldList;
	private AutopersObject autoPersObject;
	private AutopersObject combineObject;
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		if(method.getName().equals("combine")){
			combineObject= (AutopersObject) args[0];
		}else if(method.getName().equals("addField")){
			if(queryFieldList==null)
				queryFieldList=new ArrayList<AutopersObjectField>();
			queryFieldList.add((AutopersObjectField) args[0]);
			//logger.debug("add field  is "+((AutopersObjectField) args[0]).getField()+"  "+((AutopersObjectField) args[0]).getClassName());
			
		}else if(method.getName().equals("clearFields")){
			if(queryFieldList!=null)
				queryFieldList.clear();
			
		}else if(method.getName().equals("getFields")){
			 return queryFieldList;
		}
		else if(method.getName().equals("clear")){
			if(queryFieldList!=null)
				queryFieldList.clear();
			if(methods!=null)
				methods.clear();
			
		}else if(method.getName().equals("getMethods")){
			return methods;
			
		}else if(method.getName().equals("getAutopersObject")) {
			return this.autoPersObject;
		}else if(method.getName().equals("setAutopersObject")) {
			this.autoPersObject=(AutopersObject)args[0];
		}else if(method.getName().equals("getMethod")){
			if(methods==null)
				return null;
			String fieldName= (String) args[0];
			AutopersQueryMethods queryMethods= (AutopersQueryMethods) args[1];
			AutopersQueryMethod queryMethod=null;
			for(AutopersQueryMethod method1:methods){
				if(method1.getMethod().getName().equals(queryMethods.toString())){
					queryMethod=method1;
					for(AutopersObjectField field: queryMethod.getFields()){
						if(field.getField().equals(fieldName)){
							return queryMethod;
						}
					}
				}
			}

			return  null;

		}

		else{
			if(queryFieldList==null)
			{
				logger.log(Level.FINE,"set fields  is null! ");
				return proxy;
			}
			Object[] argsOp=args;
			if(method.getName().startsWith("_Like")){
				argsOp=new Object[args.length+1];
				for(int index=0;index<args.length;index++){
					argsOp[index]=args[index];
				}
				if(method.getName().equals("_Like")){
					argsOp[argsOp.length-1]=0;
				}else if(method.getName().equals("_LikeStart")){
					argsOp[argsOp.length-1]=-1;
				}else if(method.getName().equals("_LikeEnd")){
					argsOp[argsOp.length-1]=1;
				}
			}
			AutopersQueryMethod autopersQueryMethod=new AutopersQueryMethod(proxy, method, argsOp);
			autopersQueryMethod.setFields(queryFieldList);
			if(combineObject!=null)
			{
				//实现组合类查询
				if(combineObject._Query()!=null&&combineObject._Query().getFields()!=null&&combineObject._Query().getFields().size()>0)
				{
					AutopersObjectField autoPersObjectField=combineObject._Query().getFields().get(0);
					autopersQueryMethod.setCombineField(autoPersObjectField);
					combineObject._Query().clearFields();
				}
			}
		//	logger.debug("set fields size is "+queryFieldList.size());
			queryFieldList=new ArrayList<AutopersObjectField>();
			if(methods==null)
				methods=new ArrayList<AutopersQueryMethod>();
			methods.add(autopersQueryMethod);
			if(method.getName().endsWith("_F")){
				AutopersFunction autopersFunction= AutopersFunctionFactory.newAutopersFunction();
				autopersQueryMethod.setFunction(autopersFunction);
				return autopersFunction;
			}

		}
		return proxy;
	}

}

package org.violetime.autopers.query;

import java.lang.reflect.Method;
import java.util.List;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;

public class AutopersQueryMethod {
	private Object proxy;
	private Method method;
	private Object[] args;
	private List<AutopersObjectField>  fields;
	private AutopersObjectField combineField;
	private AutopersFunction function;

	public AutopersFunction getFunction() {
		return function;
	}

	public void setFunction(AutopersFunction function) {
		this.function = function;
	}

	public AutopersObjectField getCombineField() {
		return combineField;
	}

	public void setCombineField(AutopersObjectField combineField) {
		this.combineField = combineField;
	}

	public AutopersQueryMethod(Object proxy, Method method, Object[] args) {
		super();
		this.proxy = proxy;
		this.method = method;
		this.args = args;
	}
	public AutopersObjectField getField(String fieldName){
		if(fields==null)
			return  null;
		for(AutopersObjectField field:fields){
			if(field.getField().equals(fieldName)){
				return field;
			}
		}
		return  null;

	}
	public List<AutopersObjectField> getFields() {
		return fields;
	}

	public void setFields(List<AutopersObjectField> fields) {
		this.fields = fields;
	}

	public Object getProxy() {
		return proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	

}

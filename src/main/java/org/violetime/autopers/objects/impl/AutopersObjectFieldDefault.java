package org.violetime.autopers.objects.impl;
import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.objects.AutopersObjectField;
;import java.lang.reflect.Method;

/**
 *数据库实体类 属性定义
 * @author taoyo
 *
 */
public class AutopersObjectFieldDefault implements AutopersObjectField {

	private  boolean result;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	private Object value;
	private String className;
	private String field;
	private String comment;
	public String getClassName() {
		return className;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getFieldClassName() {
		return null;
	}

	@Override
	public void setFieldClassName(String fieldClassName) {

	}

	private boolean isCombine=false;
	public boolean isCombine() {
		return isCombine;
	}
	public void setCombine(boolean combine) {
		isCombine = combine;
	}
	public String getCombineField() {
		return combineField;
	}
	public void setCombineField(String combineField) {
		this.combineField = combineField;
	}
	private String combineField;
	@Override
	public void setQueryFunction(AutopersFunction queryFunction) {

	}

	@Override
	public AutopersFunction getQueryFunction() {
		return null;
	}


	/**
	 * 构造
	 * @param object
	 * @param field
	 */
	public AutopersObjectFieldDefault(Object object,String field,String className){
		this.value=object;
		this.field=field;
		this.className=className;
		
	}
	/** 
	 *  if isValue is true then the function compare with this.value
	 *  else if compare with this.className and this.field
	 * 
	 * @param autoPersObjectField
	 * @param isValue
	 * @return
	 */
	public boolean equals(AutopersObjectField autoPersObjectField,boolean isValue){
		try{
			if(this!=null&&autoPersObjectField!=null){
				if(this.getClassName().equals(autoPersObjectField.getClassName())){
					if(this.getField().equals(autoPersObjectField.getField())){
						if(!isValue)
							return true;
						if(isValue&&this.getValue().equals(autoPersObjectField.getValue())){
							return true;
						}
					}
				}
			}
		}catch(Exception exception){
			exception.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * 构造
	 * @param start
	 * @param length
	 * @return
	 */
	public AutopersObjectField substring(int start,int length){
		
		
		return  this;
	}
	public AutopersObjectField indexOf(String value){
		
		
		return  this;
	}
	public AutopersObjectField lastIndexOf(String value){
	
		
		return  this;
	}
	public AutopersObjectField toDate(String value,String formate){
		
		
		return this;
	}

	@Override
	public AutopersObjectField clone() {
		return null;
	}
}

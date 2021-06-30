package org.violetime.autopers.objects;

import org.violetime.autopers.function.AutopersFunction;

import java.lang.reflect.Method;

public interface AutopersObjectField{

	public boolean isResult();
	public void setResult(boolean result) ;
    public String getClassName() ;
	public Object getValue() ;
	public void setValue(Object value);
	public String getField() ;
	public void setField(String field) ;
    public void setClassName(String className) ;
	public String getComment() ;
	public void setComment(String comment);
	public String getFieldClassName();
	public  void setFieldClassName(String fieldClassName);
	public boolean isCombine();

	public void setCombine(boolean combine);

	public String getCombineField() ;

	public void setCombineField(String field) ;


	public void setQueryFunction(AutopersFunction queryFunction);
	public AutopersFunction getQueryFunction();
    /** 
	 *  if isValue is true then the function compare with this.value
	 *  else if compare with this.className and this.field
	 * 
	 * @param autoPersObjectField
	 * @param isValue
	 * @return
	 */
	public boolean equals(AutopersObjectField autoPersObjectField,boolean isValue);
	/**
	 * 构造
	 * @param start
	 * @param length
	 * @return
	 */
	public AutopersObjectField substring(int start,int length);
	public AutopersObjectField indexOf(String value);
	public AutopersObjectField lastIndexOf(String value);
	public AutopersObjectField toDate(String value,String formate);
	public AutopersObjectField clone();

}
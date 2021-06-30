package org.violetime.autopers.query.objects;

import java.util.List;
import java.util.Set;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.units.AutopersObjectsUnit;

public class Like  implements AutopersPlatformInvoke {
	private List<AutopersObjectField> fieldList;
	private Set<IAutopersMappingField> mappingFields;
	private Object[] args;
	private String className;
	private AutopersFunction function;
	private AutopersPlatform platform;
	private AutopersMappingClass mappingClass;
	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		if(fieldList!=null&&fieldList.size()>0&&args!=null&&args.length>0) {
			if(args[1]==null)
				return null;
			int position=0;
			if(args[2]!=null)
				position= (int) args[2];
			IAutopersMappingField field = AutopersObjectsUnit.getMappingField(fieldList.get(0).getField(),mappingClass,mappingFields);
			String val= AutopersObjectsUnit.getSqlValueByField(field,args[1]);
			if(function!=null){
				String sql=function.getSQl( className+"."+field.getColumn(),platform)+" like ";
				if(position==0){
					sql+="CONCAT('%',"+val+",'%')";
				}else if(position<0){
					sql+="CONCAT('%',"+val+")";
				}else{
					sql+="CONCAT("+val+",'%')";
				}
				return sql;
			}
			String result=className+"."+field.getColumn()+" like ";
			if(position==0){
				result+="CONCAT('%',"+val+",'%')";
			}else if(position<0){
				result+="CONCAT('%',"+val+")";
			}else{
				result+="CONCAT("+val+",'%')";
			}
			return result;
		}else {
			System.out.println(fieldList);
			System.out.println(args);
			return null;
		}
		
		
	
	}

	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}


}

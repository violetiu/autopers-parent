package org.violetime.autopers.query.objects;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.units.AutopersObjectsUnit;

import java.util.List;
import java.util.Set;

public class GroupBy  implements AutopersPlatformInvoke {

	private List<AutopersObjectField> fieldList;
	private Object[] args;
	private String className;
	protected Set<IAutopersMappingField> mappingFields;

	private AutopersMappingClass mappingClass;

	private AutopersFunction function;
	private AutopersPlatform platform;


	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		if(fieldList!=null&&fieldList.size()>0&&args!=null&&args.length>0) {
			String result="";
			IAutopersMappingField field = AutopersObjectsUnit.getMappingField(fieldList.get(0).getField(),mappingClass,mappingFields);
			if(function!=null){
				result=" group by "+function.getSQl(field.getColumn(),platform);
			}else
				result=  " group by "+field.getColumn()+"";
			if(fieldList.size()>1){
				 field = AutopersObjectsUnit.getMappingField(fieldList.get(1).getField(),mappingClass,mappingFields);
				if(function!=null){
					result+=","+function.getSQl(field.getColumn(),platform);
				}else
					result+=  ","+field.getColumn()+"";
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

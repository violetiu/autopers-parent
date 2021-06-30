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

public class Unequals  implements AutopersPlatformInvoke {
	private List<AutopersObjectField> fieldList;
	private Object[] args;
	private String className;
	protected Set<IAutopersMappingField> mappingFields;
	private AutopersFunction function;
	private AutopersPlatform platform;
	private AutopersMappingClass mappingClass;
	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		if(fieldList!=null&&fieldList.size()>0&&args!=null&&args.length>0) {
			IAutopersMappingField field = AutopersObjectsUnit.getMappingField(fieldList.get(0).getField(),mappingClass,mappingFields);
			if(args[1]==null) {
				if(function!=null)
					return function.getSQl(fieldList.get(0).getField(),platform)+" is not null";
				return  className + "." + field.getColumn() +" is not null";
			}else {
				String val= AutopersObjectsUnit.getSqlValueByField(field,args[1]);
				if(function!=null)
					return function.getSQl(className+"."+field.getColumn(),platform)+" <> "+val+"";
				return className+"."+field.getColumn()+" <> "+val+"";
			}
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

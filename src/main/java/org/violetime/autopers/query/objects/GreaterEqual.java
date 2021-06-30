package org.violetime.autopers.query.objects;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.units.AutopersObjectsUnit;

import java.util.List;
import java.util.Set;

public class GreaterEqual implements AutopersPlatformInvoke {

	private List<AutopersObjectField> fieldList;
	private Object[] args;
	private String className;
	private AutopersFunction function;
	private AutopersPlatform platform;
	protected Set<IAutopersMappingField> mappingFields;

	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		if(fieldList!=null&&fieldList.size()>0&&args!=null&&args.length>0) {
			if(args[1]==null)
				return null;
			IAutopersMappingField field = AutopersObjectsUnit.getMappingField(fieldList.get(0).getField(),mappingClass,mappingFields);
			String val= AutopersObjectsUnit.getSqlValueByField(field,args[1]);
			if(function!=null){
				return function.getSQl(className+"."+ field.getColumn(),platform)+">="+val+" ";
			}
			return className+"."+ field.getColumn()+">="+val+" ";
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
	private AutopersMappingClass mappingClass;

}

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

public class OrderBy   implements AutopersPlatformInvoke {

	private List<AutopersObjectField> fieldList;
	private Object[] args;
	private String className;
	private AutopersMappingClass mappingClass;
	private AutopersFunction function;
	private AutopersPlatform platform;
	protected Set<IAutopersMappingField> mappingFields;
	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		if(fieldList!=null&&fieldList.size()>0&&args!=null&&args.length>0) {
			IAutopersMappingField field = AutopersObjectsUnit.getMappingField(fieldList.get(0).getField(),mappingClass,mappingFields);
			if(args.length==2) {
				Boolean val=(Boolean)args[1];
				if(val==null||val) {
					if(function!=null)
						return  " order by "+function.getSQl(className+"."+field.getColumn(),platform);
					return  " order by "+className+"."+field.getColumn();
				}else {
					if(function!=null)
						return  " order by "+function.getSQl(className+"."+field.getColumn(),platform)+" desc ";
					return  " order by "+className+"."+field.getColumn()+" desc ";
				}
			}else if(args.length==3) {
				IAutopersMappingField fieldB = AutopersObjectsUnit.getMappingField(fieldList.get(1).getField(),mappingClass,mappingFields);
				Boolean val=(Boolean)args[2];
				if(val==null||val) {
					if(function!=null)
						return  " order by "+function.getSQl(className+"."+field.getColumn(),platform)+","+function.getSQl(className+"."+fieldB.getColumn(),platform);
					return  " order by "+className+"."+field.getColumn()+","+className+"."+fieldB.getColumn();
				}else {
					if(function!=null)
						return  " order by "+function.getSQl(className+"."+field.getColumn(),platform)+","+function.getSQl(className+"."+fieldB.getColumn(),platform)+" desc";
					return  " order by "+className+"."+field.getColumn()+","+className+"."+fieldB.getColumn()+" desc";
				}
			}
			return null;
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

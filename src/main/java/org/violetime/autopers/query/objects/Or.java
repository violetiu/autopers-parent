package org.violetime.autopers.query.objects;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.tool.AutopersQueryTool;

import java.util.List;

public class Or implements AutopersPlatformInvoke {

	private List<AutopersObjectField> fieldList;
	private Object[] args;
	private String className;
	private AutopersFunction function;
	private AutopersPlatform platform;
	private AutopersSession autoPersSession;

	@Override
	public Object invoke() throws Throwable {
		// TODO Auto-generated method stub
		if(args==null||args.length==0)
			return " or ";

		if(args.length==1) {
			AutopersQuery autopersQuery= (AutopersQuery) args[0];
			return " or ("+ AutopersQueryTool.getQuerySQL(autopersQuery,platform,autoPersSession,mappingClass)+")";
		}
		if(args.length==2) {
			AutopersQuery autopersQueryA= (AutopersQuery) args[0];
			AutopersQuery autopersQueryB= (AutopersQuery) args[0];
			return " and ("+ AutopersQueryTool.getQuerySQL(autopersQueryA,platform,autoPersSession,mappingClass)+") or ("+AutopersQueryTool.getQuerySQL(autopersQueryB,platform,autoPersSession,mappingClass)+")";
		}
		return " or ";
	}


	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
	private AutopersMappingClass mappingClass;

}

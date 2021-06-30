package org.violetime.autopers.objects;

import java.util.List;

import org.violetime.autopers.query.AutopersQuery;

public interface AutopersCombine {
	/**
	 * 
	 * 组合实体类中子类之间的关系定义。框架内置方法，不建议用户调用。
	 * 
	 * @param objects
	 */
	public AutopersQuery combine(List<AutopersObject> objects);

}

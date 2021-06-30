package org.violetime.autopers.query;

import org.violetime.autopers.platform.AutopersPlatform;

/**
 * 分页接口
 * @author violet
 *
 */
public interface AutopersQueryPage {

	/**
	 * 设置当前页
	 * @param page
	 */
	public void setPage(Integer page);
	/**
	 * 获取当前页
	 * @return
	 */
	public Integer getPage();
	/**
	 * 设置页面条数
	 * @param num
	 */
	public void setNum(Integer num);
	/**
	 * 获取页面条数
	 * @return
	 */
	public Integer getNum();

	/**
	 * 获取分页sql
	 *
	 * @param platform
	 * @return
	 */
	public String getSql(String sql,AutopersPlatform platform);


}

package org.violetime.autopers.query.impl;

import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.platform.AutopersPlatformPackage;
import org.violetime.autopers.query.AutopersQueryPage;

public class AutopersQueryPageDefault implements AutopersQueryPage {
	
	private Integer page;
	private Integer num;

	@Override
	public void setPage(Integer page) {
		// TODO Auto-generated method stub
		this.page=page;
	}

	@Override
	public Integer getPage() {
		// TODO Auto-generated method stub
		return page;
	}

	@Override
	public void setNum(Integer num) {
		// TODO Auto-generated method stub
		this.num=num;
	}

	@Override
	public Integer getNum() {
		// TODO Auto-generated method stub
		return num;
	}

	@Override
	public String getSql(String sql, AutopersPlatform platform) {
		if(platform==null||platform.getPackageMap()==null)
			return null;
		AutopersPlatformPackage platformPackage= platform.getPackageMap().get(AutopersQueryPage.class.getName());
		if(platformPackage==null)
			return null;
		 AutopersPlatformObject platformObject= platformPackage.getObjectMap().get("getSql");
		 if(platformObject==null)
		 	return null;
		 if(platformObject.getPropertys()==null){
		 	return null;
		 }
		String select= platformObject.getPropertys().get("select");
		String column= platformObject.getPropertys().get("column");
		String where= platformObject.getPropertys().get("where");

		if(column!=null&&column.trim().length()>0){
			sql="select "+column+" "+ sql.substring("select".length());

		}

		if(select!=null||select.trim().length()>0)
		{
			sql="select * from ("+sql+") QueryPage";
			if(where!=null&&where.trim().length()>0){

				if(where.contains("[page]*[num]")){
					where=where.replace("[page]*[num]",page*num+"");
				}
				if(where.contains("([page]+1)*[num]")){
					where=where.replace("([page]+1)*[num]",(page+1)*num+"");
				}
				if(where.contains("[page]")){
					where=where.replace("[page]",page+"");
				}
				if(where.contains("[num]")){
					where=where.replace("[num]",num+"");
				}



				return sql+=" "+where;
			}

		}else if(where!=null&&where.trim().length()>0){

			if(where.contains("[page]")){
				where=where.replace("[page]",page+"");
			}
			if(where.contains("[num]")){
				where=where.replace("[num]",num+"");
			}
			return sql+=" "+where;
		}


		return sql;
	}



}

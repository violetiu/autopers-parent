package org.violetime.autopers.creator;

import java.util.HashMap;

/**
 * 自动写入数据库实体类和xml实体配置文件
 * @author taoyo
 *
 */
public interface AutoWriteObjectXml {

	public String autoWrite();
	public void setAppPath(String path);
	public String getObjectPath() ;
	
	public String getXmlPath();

	public String getMappingPath();

	public String getIsAuto();
	public void setDataTypeMap(HashMap<String, String> dataTypeMap);


	public void setObjectPath(String objectPath) ;

	public void setMappingPath(String mappingPath);

	public void setConfigPath(String configPath);

	public void isCreator();

}

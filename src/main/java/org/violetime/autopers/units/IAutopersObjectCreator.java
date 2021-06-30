package org.violetime.autopers.units;

public interface IAutopersObjectCreator {
    public String getWebApp();
    public void setWebApp(String webApp) ;
    public String getObjectPath();
    public void setObjectPath(String objectPath);
    public String getMappingPath();
    public void setMappingPath(String mappingPath);
    public String getConfigPath() ;
    public void setConfigPath(String configPath);
    public void create();
}

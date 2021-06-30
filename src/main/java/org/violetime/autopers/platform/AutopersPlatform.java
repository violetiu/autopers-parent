package org.violetime.autopers.platform;

import java.util.HashMap;

/**
 * 数据平台
 */
public class AutopersPlatform {

    private String name ,jdbc;
    private  HashMap<String,AutopersPlatformPackage> packageMap;

    /**
     * @return the jdbc
     */
    public String getJdbc() {
        return jdbc;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param jdbc the jdbc to set
     */
    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the packageMap
     */
    public HashMap<String, AutopersPlatformPackage> getPackageMap() {
        return packageMap;
    }
    /**
     * @param packageMap the packageMap to set
     */
    public void setPackageMap(HashMap<String, AutopersPlatformPackage> packageMap) {
        this.packageMap = packageMap;
    }
    

}

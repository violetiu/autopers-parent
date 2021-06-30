package org.violetime.autopers.platform;

import java.util.HashMap;

/**
 * 
 * 
 * 
 */
public class AutopersPlatformObject {

    private String name;
    private String className="";
    
    public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	private HashMap<String,String> propertys;
    /**
     * @return the propertys
     */
    public HashMap<String, String> getPropertys() {
        return propertys;
    }
    /**
     * @param propertys the propertys to set
     */
    public void setPropertys(HashMap<String, String> propertys) {
        this.propertys = propertys;
    }
    
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    };
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    };

}

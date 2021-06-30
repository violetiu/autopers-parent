package org.violetime.autopers.platform;

import java.util.HashMap;

/**
 * 
 * 
 * 
 */
public class AutopersPlatformPackage {

    private String path;

    private HashMap<String,AutopersPlatformObject> objectMap;

    /**
     * @return the objectMap
     */
    public HashMap<String, AutopersPlatformObject> getObjectMap() {
        return objectMap;
    }
    /**
     * @param objectMap the objectMap to set
     */
    public void setObjectMap(HashMap<String, AutopersPlatformObject> objectMap) {
        this.objectMap = objectMap;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    

}

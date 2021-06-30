package org.violetime.autopers.platform;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.violetime.autopers.database.DataBaseSource;
/**
 * 
 * @author taoyongwen
 */
public class AutopersPlatformFactory {

    private static  HashMap<String,AutopersPlatform> platformMap;
    private final static Logger logger = Logger.getLogger(AutopersPlatformFactory.class.getName());
    /**
     * @return the platformMap
     */
    public static HashMap<String, AutopersPlatform> getPlatformMap() {
        return platformMap;
    }
    /**
     * @param platformMap the platformMap to set
     */
    public static void setPlatformMap(HashMap<String, AutopersPlatform> platformMap) {
        AutopersPlatformFactory.platformMap = platformMap;
    }

    /**
     * 
     * @param dataBaseSource
     * @return
     */
    public static AutopersPlatform getPlatform(DataBaseSource dataBaseSource){

        if(platformMap==null||platformMap.size()==0){
           logger.log(Level.FINE,"dataBaseSource  not found platform!");
            return null;
        }
        String name= dataBaseSource.getName();
        AutopersPlatform autoPersPlatform=platformMap.get(name);
        if(autoPersPlatform!=null)
        return autoPersPlatform;
        String jdbc=dataBaseSource.getDriverClassName();
        return platformMap.get(jdbc);
    }
    public static String getDataBaseSourceByPlatformName(String platformName){

        if(platformMap==null||platformMap.size()==0){
           logger.log(Level.FINE,"dataBaseSource  not found platform!");
            return null;
        }
        for(Object key : platformMap.keySet()) {
        	AutopersPlatform persPlatform=platformMap.get(key);
        	if(persPlatform.getName().contentEquals(platformName)) {
        		return key.toString();
        		
        		
        	}
        }
        return null;
    }

    

}

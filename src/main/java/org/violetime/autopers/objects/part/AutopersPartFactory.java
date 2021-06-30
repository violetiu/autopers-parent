package org.violetime.autopers.objects.part;

import org.violetime.autopers.objects.AutopersObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AutopersPartFactory {
    private  static Map<String,Class<AutopersPart>> partMap;
    public  static void put(String objectName,Class<AutopersPart> part){
        if(partMap==null)
            partMap=new HashMap<>();
        partMap.put(objectName,part);
    }
    public  static  Class<AutopersPart> get(String objectName){
        if(partMap==null)
            return null;
        return  partMap.get(objectName);
    }
    public  static  Class<AutopersPart> get(AutopersObject object){
        if(partMap==null)
            return null;
        if(object._IsCombine()){
            Class aClass=object._GetMappingClass().values().stream().findFirst().get();
            return  partMap.get(aClass.getSimpleName());
        }else{
            return  partMap.get(object._GetProxyClass().getSimpleName());
        }


    }
    private static  Map<String,AutopersPart> partObjMap;

    /**
     *
     * @param objectName - simpleName
     * @return
     */
    public static  boolean isPart(String objectName){

        if(partMap==null)
        {
//            System.out.println("partMap==null");
            return false;
        }
        Class<AutopersPart> partClass=  partMap.get(objectName);
        if(partClass==null)
        {
//            System.out.println("partClass==null");
            return false;
        }
        return  true;
    }

    /**
     *包括 组合类的判断
     * @param object
     * @return
     */
    public static  boolean isPart(AutopersObject object){
        if(partMap==null)
        {
            return false;
        }
        if(object._IsCombine()){
            Class<?> aClass= object._GetMappingClass().values().stream().findFirst().get();
           return isPart(aClass.getSimpleName());
        }else{
            return isPart(object._GetProxyClass().getSimpleName());
        }
    }
    public  static String key(String objectName, AutopersObject object){


        if(partMap==null)
        {
//            System.out.println("partMap==null");
            return null;
        }
        Class<AutopersPart> partClass=  partMap.get(objectName);
        if(partClass==null)
        {
//            System.out.println("partClass==null");
            return null;
        }
        try {
            AutopersPart  part= null;
            if(partObjMap!=null&&partObjMap.containsKey(objectName))
                part=partObjMap.get(objectName);
            else
                part=partClass.getConstructor().newInstance();

            String key=part.key(object);
            return  key;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map<String, Class<AutopersPart>> getPartMap() {
        return partMap;
    }

    public static void setPartMap(Map<String, Class<AutopersPart>> partMap) {
        AutopersPartFactory.partMap = partMap;
    }
}

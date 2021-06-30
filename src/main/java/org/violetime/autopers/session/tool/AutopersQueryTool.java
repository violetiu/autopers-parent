package org.violetime.autopers.session.tool;

import org.violetime.autopers.cache.AutopersCache;
import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.function.AutopersFunctionGroup;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.platform.AutopersPlatformPackage;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.query.AutopersQueryMethod;
import org.violetime.autopers.query.QueryObjectReturnData;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.units.PlatformInvokeUnit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutopersQueryTool {
    private final static Logger logger = Logger.getLogger(AutopersQueryTool.class.getName());

    private static Map<String, String> sqlMap;
    private static Map<String, AutopersPlatformInvoke> platformInvokeMap;

    public static void putCacheSql(String codeId, String sql) {
        AutopersCache.push("SQL:" + codeId, sql);

    }

    public static String getCacheSql(String codeId) {
        Object obj = AutopersCache.peek("SQL:" + codeId);
        if (obj == null)
            return null;
        else
            return obj.toString();
    }

    /**
     * 获取代码执行的位置，用于缓存该代码执行的sql
     * @param leve
     * @return
     */
    public static String getCodeIdFromObjectStatic(int leve) {
        try{
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
//        System.out.println("");
//        for(StackTraceElement element:stacks){
//            System.out.print(element.getClassName()+element.getLineNumber()+"\t");
//        }
//        System.out.println("");
            String codeId=stacks[leve].getClassName()+"." + stacks[leve].getLineNumber()+"l";
            //System.out.println(codeId);
            return codeId;
        }catch (Exception e){
            return null;
        }


    }

    /**
     * 结果定义中是否使用了组合函数
     *
     * @param autoPersObject
     * @return
     */
    public static boolean hasResultFunction(AutopersObject autoPersObject) {
        if (autoPersObject._GetFields() == null)
            return false;
        for (AutopersObjectField objectField : autoPersObject._GetFields().values()) {
            if (objectField.getQueryFunction() != null) {
                Map<String, Object[]> functions = objectField.getQueryFunction().getFunctions();
                if (functions != null && functions.size() > 0) {
                    for (String fun : functions.keySet()) {
                        try {
                            if (AutopersFunctionGroup.class.getMethod(fun) != null) {
                                return true;
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 拼接 sql 返回结果部分
     *
     * @param mappingClass
     * @param autoPersObject
     * @param mappingFields
     * @return
     */
    public static String getResultSQL(AutopersMappingClass mappingClass, AutopersObject autoPersObject,
                                      List<IAutopersMappingField> mappingFields, AutopersPlatform platform) throws SQLException {
        return getResultSQL(mappingClass,autoPersObject,mappingFields,platform,mappingClass.getName());
    }
    public static String getResultSQL(AutopersMappingClass mappingClass, AutopersObject autoPersObject,
                                      List<IAutopersMappingField> mappingFields, AutopersPlatform platform,String tableName) throws SQLException {
        StringBuffer sql = new StringBuffer();
        if (mappingFields == null || mappingFields.size() == 0) {
            logger.log(Level.FINE,"没有获取到mappingFields");
            sql.append("select * from ").append(tableName).append(" ")
                    .append(autoPersObject._GetProxyClass().getSimpleName());
        } else {
            StringBuffer columns = new StringBuffer();
            for (IAutopersMappingField mappingField : mappingFields) {
                if (autoPersObject._HasResult()) {
                    AutopersObjectField objectField = autoPersObject._GetFields().get(mappingField.getName());
                    if (objectField != null &&objectField.isResult()) {
                        if(objectField.getQueryFunction()!=null&&objectField.getQueryFunction().getFunctions()!=null&&objectField.getQueryFunction().getFunctions().size()>0){
                            String columeSql = objectField.getQueryFunction().getSQl(mappingField.getColumn(), platform);
                            if (columeSql == null) {
                                throw new SQLException("QueryFunction result is null ,the column is  " + mappingField.getColumn());
                            }
                            columns.append(columeSql).append(" ").append(mappingField.getColumn()).append(",");
                        }else{
                            columns.append(mappingField.getColumn()).append(",");
                        }

                    }
                } else {
                    columns.append(mappingField.getColumn()).append(",");
                }

            }
            String columnsSql = columns.toString();
            if (columnsSql.length() > 0)
                columnsSql = columnsSql.substring(0, columnsSql.length() - 1);
            sql.append("select ").append(columnsSql).append(" from ").append(tableName)
                    .append(" ").append(autoPersObject._GetProxyClass().getSimpleName());
        }
        return sql.toString();
    }

    public static StringBuffer getQuerySQL(AutopersQuery query, AutopersPlatform platform, AutopersSession persSession, AutopersMappingClass mappingClass, Set<IAutopersMappingField> autoPersMappingFields, boolean isCombine) throws Throwable {

        AutopersPlatformPackage platformPackage = platform.getPackageMap()
                .get(AutopersQuery.class.getName());
        HashMap<String, AutopersPlatformObject> objectMap = platformPackage.getObjectMap();
        List<AutopersQueryMethod> queryMethods = query.getMethods();
        StringBuffer queryMethodBuffer = new StringBuffer();
        for (AutopersQueryMethod queryMethod : queryMethods) {
            String objectName = queryMethod.getMethod().getName().replace("_F", "");
            if (objectName.startsWith("_Like")) {
                objectName = "_Like";
            }
            AutopersPlatformObject platformObject = objectMap.get(objectName);
            if (platformObject == null) {
                throw new Exception("AutopersPlatformObject is null ,method.name->"
                        + objectName);
            }
            String objectClass = platformObject.getClassName();
            Class<?> invokeClass = Class.forName(objectClass);
            AutopersPlatformInvoke platformInvoke = null;
            platformInvoke = (AutopersPlatformInvoke) invokeClass.getConstructor()
                    .newInstance();
//            if (platformInvokeMap != null) {
//                platformInvoke = platformInvokeMap.get(objectClass);
//            } else {
//                platformInvokeMap = new HashMap<>();
//            }
//            if (platformInvoke == null) {
//                platformInvoke = (AutopersPlatformInvoke) invokeClass.getConstructor()
//                        .newInstance();
//                platformInvokeMap.put(objectClass, platformInvoke);
//            }
            String className ="";
            if(mappingClass!=null&&mappingClass.getClassPath()!=null){
                className= mappingClass.getClassPath().substring(mappingClass.getClassPath().lastIndexOf(".") + 1);
            }


            if (isCombine&&queryMethod.getFields() != null) {
                className = queryMethod.getFields().get(0).getClassName();
                if (className.contains(".")) {
                    className = className.substring(className.lastIndexOf(".") + 1);
                }
            }


            for (Field field : invokeClass.getDeclaredFields()) {


                if (field.getType().equals(AutopersObjectField.class)) {
                    if (queryMethod.getCombineField() != null)
                        PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, queryMethod.getCombineField());
                    continue;

                }
                if (field.getType().equals(String.class)) {
                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, className);
                    continue;

                }
                if (field.getType().equals(AutopersFunction.class)) {
                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, queryMethod.getFunction());
                    continue;

                }

                if (field.getType().equals(AutopersPlatformObject.class)) {

                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, platformObject);
                    continue;

                }
                if (field.getType().equals(AutopersPlatform.class)) {
                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, platform);
                    continue;
                }
                if (field.getType().equals(AutopersSession.class)) {
                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, persSession);
                    continue;
                }

                if (field.getType().toString().equals("interface java.util.Set")) {

                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, autoPersMappingFields);
                    continue;
                }
                if (field.getType().toString().equals("interface java.util.List")) {


                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, queryMethod.getFields());
                    continue;
                }
                if (field.getType().equals(Method.class)) {
                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, queryMethod.getMethod());
                    continue;
                }
                if (field.getType().equals(AutopersMappingClass.class)) {
                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, mappingClass);
                    continue;
                }

                if (field.getType().equals(Object[].class)) {
                    PlatformInvokeUnit.setField(invokeClass, platformInvoke, field, queryMethod.getArgs());
                    continue;
                }
            }

            Object result = platformInvoke.invoke();
            if (result != null) {
                if (result.getClass().equals(QueryObjectReturnData.class)) {


                } else if (result.toString().trim().startsWith("group by")) {
                    if (queryMethodBuffer.toString().trim().endsWith(("and"))) {
                        queryMethodBuffer = new StringBuffer(queryMethodBuffer.toString().trim().substring(queryMethodBuffer.length() - 3));
                    } else if (queryMethodBuffer.toString().trim().endsWith(("or"))) {
                        queryMethodBuffer = new StringBuffer(queryMethodBuffer.toString().trim().substring(queryMethodBuffer.length() - 2));
                    } else if (queryMethodBuffer.indexOf("group by") > 0) {
                        result = result.toString().replace("group by", ",");
                    }
                    queryMethodBuffer.append(" " + result);

                } else {
                    if (queryMethodBuffer.length() > 0 && !queryMethodBuffer.toString().trim().endsWith(" or")) {
                        if (platformObject.getPropertys() != null && platformObject.getPropertys().get("separator") != null) {
                            queryMethodBuffer.append(platformObject.getPropertys().get("separator"));
                        } else
                            queryMethodBuffer.append(" and ");
                    }
                    queryMethodBuffer.append(result);
                }

            }

        }
        if (queryMethodBuffer.toString().trim().startsWith("and ")) {
            queryMethodBuffer = new StringBuffer(queryMethodBuffer.substring(5));
        }
        if (queryMethodBuffer.toString().trim().startsWith("or ")) {
            queryMethodBuffer = new StringBuffer(queryMethodBuffer.substring(4));
        }
        return queryMethodBuffer;
    }

    public static StringBuffer getQuerySQL(AutopersQuery query, AutopersPlatform platform, AutopersSession persSession, AutopersMappingClass mappingClass) throws Throwable {

        return getQuerySQL(query, platform, persSession, mappingClass, true);
    }

    public static StringBuffer getQuerySQL(AutopersQuery query, AutopersPlatform platform, AutopersSession persSession, AutopersMappingClass mappingClass, boolean isCombine) throws Throwable {
        Set<IAutopersMappingField> autoPersMappingFields = new HashSet<>();
        for (IAutopersMappingField field : mappingClass.getFields()) {
            autoPersMappingFields.add(field);
        }

        return getQuerySQL(query, platform, persSession, mappingClass, autoPersMappingFields,isCombine);
    }
    public void get(  List<AutopersObject> list){

    }
}

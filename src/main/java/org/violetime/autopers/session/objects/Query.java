package org.violetime.autopers.session.objects;

import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectsFactory;
import org.violetime.autopers.objects.part.AutopersPart;
import org.violetime.autopers.objects.part.AutopersPartFactory;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.query.AutopersQueryMethod;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionPool;
import org.violetime.autopers.session.tool.AutopersPartTool;
import org.violetime.autopers.session.tool.AutopersQueryTool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 保护查询语句的查询类
 *
 * @author taoyo
 */
public class Query implements AutopersPlatformInvoke {
    private final static Logger logger = Logger.getLogger(Query.class.getName());
    private AutopersSession persSession;
    private Object[] args;
    private AutopersPlatform platform;
    private String throwablePrint;
    private DataBaseSource baseSource;

    @Override
    public Object invoke() throws Throwable {

        AutopersQuery query = (AutopersQuery) args[0];
        AutopersObject autoPersObject = query.getAutopersObject();
        //是否存在分表

        boolean isPark = AutopersPartFactory.isPart(autoPersObject);
        logger.log(Level.FINE, "Preparing to query :" + isPark);
        if (isPark)
            return mulQuery(); //分表多线程查询
        else
            return simpleQuery();//单表查询
    }

    @Override
    public String throwablePrint() {
        // TODO Auto-generated method stub
        return throwablePrint;
    }

    public Object simpleQuery() throws Throwable {
        AutopersQuery query = (AutopersQuery) args[0];
        AutopersObject autoPersObject = query.getAutopersObject();
        Map<String, Class<?>> xmlClassMap = autoPersObject._GetMappingClass();
        Map<String, AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();
        long start = System.currentTimeMillis();
        String codeId = null;
        if (persSession.codeId() != null) {
            codeId = persSession.codeId();
        } else {
            codeId = AutopersQueryTool.getCodeIdFromObjectStatic(3);
        }

        if (!autoPersObject._IsCombine()) {
            Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
            AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
            if (mappingClass != null) {
                List<IAutopersMappingField> mappingFields = mappingClass.getFields();
                //缓存sql
                String sql = null;
                if (codeId != null && AutopersSessionPool.isProjectModel) {
                    if (AutopersQueryTool.getCacheSql(codeId) != null) {
                        sql = AutopersQueryTool.getCacheSql(codeId);
                    } else {
                        sql = AutopersQueryTool.getResultSQL(mappingClass, autoPersObject, mappingFields, platform);
                        AutopersQueryTool.putCacheSql(codeId, sql);
                    }
                } else {
                    sql = AutopersQueryTool.getResultSQL(mappingClass, autoPersObject, mappingFields, platform);
                }
                logger.info("Q" + persSession.getIndex() + " " + sql);
                StringBuffer queryMethodBuffer = AutopersQueryTool.getQuerySQL(query, platform, persSession, mappingClass, false);
                if (queryMethodBuffer.length() > 0) {
                    if (queryMethodBuffer.toString().trim().startsWith("order by ") || queryMethodBuffer.toString().trim().startsWith("group by"))
                        sql += " " + queryMethodBuffer.toString();
                    else
                        sql += " where  " + queryMethodBuffer.toString();
                    if (queryMethodBuffer.indexOf("group by") >= 0 && !autoPersObject._HasResult()) {
                        throw new SQLException("The result is missing in the group query!");
                    }
                    if (AutopersQueryTool.hasResultFunction(autoPersObject) && queryMethodBuffer.indexOf("group by") < 0) {
                        throw new SQLException("The result funcation can't use in the no group query!");
                    }
                }
                if (autoPersObject._Page() != null) {
                    sql = autoPersObject._Page().getSql(sql, platform);
                }

                throwablePrint = sql;
                PreparedStatement preparedStatement = persSession.getPreparedStatement();
                ResultSet resultSet = preparedStatement.executeQuery(sql);
                List<AutopersObject> results = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    AutopersObject persObject = (AutopersObject) AutopersObjectsFactory
                            .newInstanceObject(autoPersObject._GetProxyClass());
                    HashMap<String, AutopersObjectField> filedMap = persObject._GetFields();
                    for (IAutopersMappingField mappingField : mappingFields) {
                        try {
                            Object sqlVal = resultSet.getObject(mappingField.getColumn());
                            if (sqlVal != null) {
                                AutopersObjectField objectField = null;

                                if (mappingField.getJavatype().equals(Integer.class.getName())) {
                                    Object newVal = Integer.parseInt(sqlVal.toString());

                                    objectField = AutopersObjectsFactory.newInstanceField(newVal,
                                            mappingField.getName(), xmlClass.getName());
                                } else {
                                    objectField = AutopersObjectsFactory.newInstanceField(sqlVal,
                                            mappingField.getName(), xmlClass.getName());
                                }

                                filedMap.put(mappingField.getName(), objectField);
                            }
                        } catch (Exception e) {

                        }
                    }
                    results.add(persObject);
                }
                preparedStatement.close();
                logger.info("Q" + persSession.getIndex() + " Total " + results.size() + ", execution time " + (System.currentTimeMillis() - start) + "ms");
                return results;
            }

        } else {
            //主查询语句
            //使用被组合的对象表使用联合查询 select .. from a,b where a.key =b.key
            StringBuffer querySql = new StringBuffer("select ");
            Map<String, IAutopersMappingField> fieldMap = new HashMap<>();
            Map<String, Class> classMap = new HashMap<>();
            Map<String, AutopersMappingClass> mappingMap = new HashMap<>();
            for (Object key : xmlClassMap.keySet()) {
                Class<?> xmlClass = xmlClassMap.get(key);
                AutopersMappingClass mappingClass = mappingClassMap.get(key);
                if (mappingClass != null) {
                    for (IAutopersMappingField mappingField : mappingClass.getFields()) {
                        if (fieldMap.get(mappingField.getColumn()) == null) {
                            fieldMap.put(mappingField.getColumn(), mappingField);
                            classMap.put(mappingField.getColumn(), xmlClass);
                            mappingMap.put(mappingField.getColumn(), mappingClass);
                        }
                    }
                } else {
                    logger.log(Level.FINE, "mappingClass is null");
                }
            }
            Set<IAutopersMappingField> fieldSet = new HashSet<>();
            for (Object key : fieldMap.keySet()) {
                IAutopersMappingField mappingField = fieldMap.get(key);
                Class<?> xmlClass = classMap.get(key);
                AutopersMappingClass mappingClass = mappingMap.get(key);
                if (autoPersObject._HasResult()) {
                    AutopersObjectField objectField = autoPersObject._GetFields().get(mappingField.getName());
                    if (objectField != null && objectField.getQueryFunction() != null) {
                        String columeSql = objectField.getQueryFunction().getSQl(mappingField.getColumn(), platform);
                        if (columeSql == null) {
                            throw new SQLException("QueryFunction result is null ,the column is  " + mappingField.getColumn());
                        }

                        querySql.append(" " + columeSql + " " + mappingField.getColumn() + ",");
                    }
                } else {
                    querySql.append(" " + xmlClass.getSimpleName() + "." + mappingField.getColumn() + " " + mappingField.getColumn() + ",");
                }
                fieldSet.add(mappingField);
            }
            if (querySql.toString().endsWith(","))
                querySql = new StringBuffer(querySql.substring(0, querySql.length() - 1));
            //
            querySql.append(" from  ");
            for (Object key : xmlClassMap.keySet()) {
                Class<?> xmlClass = xmlClassMap.get(key);
                AutopersMappingClass mappingClass = mappingClassMap.get(key);
                querySql.append(mappingClass.getName() + " " + xmlClass.getSimpleName() + ",");

            }
            if (querySql.toString().endsWith(","))
                querySql = new StringBuffer(querySql.substring(0, querySql.length() - 1));
            //
            querySql.append(" where ");
            for (Object key : fieldMap.keySet()) {
                IAutopersMappingField field = fieldMap.get(key);
                Class<?> xmlClass = classMap.get(key);
                AutopersMappingClass mappingClass = mappingMap.get(key);
                AutopersObjectField objectField = autoPersObject._GetFields().get(field.getName());
                if (objectField != null && objectField.isCombine()) {
                    querySql.append(" " + xmlClass.getSimpleName() + "." + field.getColumn() + "=" + objectField.getCombineField() + " and");
                } else {
                    logger.log(Level.FINE, "objectField is null");
                }
            }
            logger.log(Level.FINE, persSession.getIndex() + " " + querySql.toString());
            //主查询语句的条件
            StringBuffer queryMethodBuffer = AutopersQueryTool.getQuerySQL(query, platform, persSession, null, fieldSet, true);
            if (queryMethodBuffer.length() > 0) {

                querySql.append(" " + queryMethodBuffer.toString());

            }
            if (querySql.toString().endsWith("and")) {
                querySql = new StringBuffer(querySql.substring(0, querySql.length() - 3));
            }

            if (autoPersObject._Page() != null) {
                querySql = new StringBuffer(autoPersObject._Page().getSql(querySql.toString(), platform));
            }
            throwablePrint = persSession.getIndex() + " " + querySql.toString();
            //开始执行查询方法
            PreparedStatement preparedStatement = persSession.getPreparedStatement();
            ResultSet resultSet = preparedStatement.executeQuery(querySql.toString());
            List<AutopersObject> results = new ArrayList<>();
            //组装数据，并集合的方式返回
            while (resultSet != null && resultSet.next()) {
                AutopersObject persObject = (AutopersObject) AutopersObjectsFactory
                        .newInstanceObject(autoPersObject._GetProxyClass());
                /**
                 * 组装对象原数据值
                 */
                for (Object key : fieldMap.keySet()) {
                    IAutopersMappingField mappingField = fieldMap.get(key);
                    Class<?> xmlClass = classMap.get(key);
                    AutopersMappingClass mappingClass = mappingMap.get(key);
                    HashMap<String, AutopersObjectField> filedMap = persObject._GetFields();
                    try {
                        Object sqlVal = resultSet.getObject(mappingField.getColumn());
                        if (sqlVal != null) {
                            AutopersObjectField objectField = AutopersObjectsFactory.newInstanceField(sqlVal,
                                    mappingField.getName(), xmlClass.getName());
                            filedMap.put(mappingField.getName(), objectField);
                        }
                    } catch (Exception e) {
                        // logger.debug("No found column :"+mappingField.getColumn());
                    }

                }
                /**
                 * 组合类中字典注解方法，设置数据
                 */
                results.add(persObject);
            }
            preparedStatement.close();
            return results;
        }

        return null;
    }

    public Object mulQuery() throws Throwable {

        AutopersQuery query = (AutopersQuery) args[0];
        AutopersObject autoPersObject = query.getAutopersObject();
        Map<String, Class<?>> xmlClassMap = autoPersObject._GetMappingClass();
        Map<String, AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();
        long start = System.currentTimeMillis();
        String codeId = null;
        if (persSession.codeId() != null) {
            codeId = persSession.codeId();
        } else {
            codeId = AutopersQueryTool.getCodeIdFromObjectStatic(3);
        }
        if (!autoPersObject._IsCombine()) {
            Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
            AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
            Class parkClass = AutopersPartFactory.get(autoPersObject);
            AutopersPart part = (AutopersPart) parkClass.getConstructor().newInstance();
            List<String> tables = AutopersPartTool.getPartTables(baseSource, mappingClass.getName(), part, query);
            if (tables != null) {
//                logger.info("table  size is" + tables.size());
                List<QueryThead> queryTheads = new ArrayList<>();
                for (String table : tables) {
                    QueryThead queryThead = new QueryThead();
                    queryThead.setQuery(query);
                    queryThead.setPlatform(platform);
                    queryThead.setAutoPersObject(autoPersObject);
                    queryThead.setCodeId(codeId);
                    queryThead.setMappingClass(mappingClass);
                    queryThead.setStart(start);
                    queryThead.setTableName(table);
                    queryThead.setXmlClass(xmlClass);
                    queryThead.setName(table);
                    queryThead.setEnd(false);
                    queryThead.setPersSession(persSession.copySession());
                    queryThead.getPersSession().codeId(null);
                    queryThead.start();
                    queryTheads.add(queryThead);
                }
                while (true) {
                    boolean isEnd = true;
                    for (QueryThead queryThead : queryTheads) {
                        if (!queryThead.isEnd()) {
                            isEnd = false;
                            break;
                        }
                    }
                    if (isEnd)
                        break;
                    Thread.sleep(500);
                }
                return groupData(autoPersObject, query, queryTheads, null);
            } else {
                ///  logger.info("table  size is null !");
            }

        } else {
            Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
            AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
            Class parkClass = AutopersPartFactory.get(autoPersObject);
            AutopersPart part = (AutopersPart) parkClass.getConstructor().newInstance();
            List<String> tables = AutopersPartTool.getPartTables(baseSource, mappingClass.getName(), part, query);

            if (tables != null) {
                //   logger.info("table  size is" + tables.size());
                List<QueryCombineThead> queryTheads = new ArrayList<>();
                for (String table : tables) {
                    QueryCombineThead queryThead = new QueryCombineThead();
                    queryThead.setPlatform(platform);
                    queryThead.setQuery(query);
                    queryThead.setAutoPersObject(autoPersObject);
                    queryThead.setCodeId(codeId);
                    queryThead.setMappingClassMap(mappingClassMap);
                    queryThead.setStart(start);
                    queryThead.setTableName(table);
                    queryThead.setXmlClassMap(xmlClassMap);
                    queryThead.setName(table);
                    queryThead.setEnd(false);
                    queryThead.setPersSession(persSession.copySession());
                    queryThead.getPersSession().codeId(null);
                    queryThead.start();
                    queryTheads.add(queryThead);
                }
                while (true) {
                    boolean isEnd = true;
                    for (QueryCombineThead queryThead : queryTheads) {
                        if (!queryThead.isEnd()) {
                            isEnd = false;
                            break;
                        }
                    }
                    if (isEnd)
                        break;
                    Thread.sleep(500);
                }

                return groupData(autoPersObject, query, null, queryTheads);
            } else {

                logger.info("table  size is null !");

            }


        }


        return null;
    }

    public List<AutopersObject> groupData(AutopersObject autoPersObject, AutopersQuery query, List<QueryThead> queryTheads, List<QueryCombineThead> queryCombineTheads) {
        //System.out.println("GroupData :"+autoPersObject._HasResult());
        if (autoPersObject._HasResult()) {
            //组合类方法合并数据
            //寻找group by 字段
            List<AutopersObjectField> groupFields = new ArrayList<>();
            for (AutopersObjectField objectField : autoPersObject._GetFields().values()) {
              //  System.out.println(objectField.getField()+"\t"+objectField.isResult());
                if (objectField.isResult()) {
                    AutopersFunction function = objectField.getQueryFunction();
                    if (function != null) {
                        Map<String, Object[]> functions = function.getFunctions();
                        if (functions != null && functions.size() > 0) {
                            for (String method : functions.keySet()) {
                                //System.out.println(method);
                                Object[] args = functions.get(method);
                                if (method.equals("_None") || method.equals("_SubString")) {
                                    groupFields.add(objectField);
                                    break;
                                }
                            }
                        } else {
                            //考虑没有 设置 _Noned的默认情况
                            groupFields.add(objectField);
                        }
                    }

                }


            }
//            if(queryTheads!=null)
//                 System.out.println(" thread count is " + queryTheads.size());
//            else
//                System.out.println(" thread count is " + queryCombineTheads.size());
            //logger.info("Q "+persSession.getIndex() +" group by size:"+groupFields.size());

            //对数据，按照groupfield分组
            Map<String, List<AutopersObject>> dataMap = new HashMap<>();
            if (queryTheads != null)
                for (QueryThead queryThead : queryTheads) {
                    List<AutopersObject> result = queryThead.getResult();
                    if (result != null) {
                        for (AutopersObject dataObject : result) {
                            String dataKey = "";
                            for (AutopersObjectField field : groupFields) {
                                Object value = dataObject._GetFields().get(field.getField()).getValue();
                                dataKey += value + "-";
                            }
                            List<AutopersObject> dataList = dataMap.get(dataKey);
                            if (dataList == null) {
                                dataList = new ArrayList<>();
                                dataMap.put(dataKey, dataList);
                            }
                            dataList.add(dataObject);
                        }
                    }
                }
            else if (queryCombineTheads != null)
                for (QueryCombineThead queryThead : queryCombineTheads) {
                    List<AutopersObject> result = queryThead.getResult();
                    if (result != null) {
                        for (AutopersObject dataObject : result) {
                            String dataKey = "";
                            for (AutopersObjectField field : groupFields) {
                                Object value = dataObject._GetFields().get(field.getField()).getValue();
                                dataKey += value + "-";
                            }
                            List<AutopersObject> dataList = dataMap.get(dataKey);
                            if (dataList == null) {
                                dataList = new ArrayList<>();
                                dataMap.put(dataKey, dataList);
                            }
                            dataList.add(dataObject);
                        }
                    }
                }
//            logger.info("Q "+persSession.getIndex() +" group by data size:"+dataMap.size());
            //处理分组数据中 的 各类方法
            List<AutopersObject> result = new ArrayList<>();
            for (String dataKey : dataMap.keySet()) {

                List<AutopersObject> dataList = dataMap.get(dataKey);

                AutopersObject object = AutopersObjectsFactory.newInstanceObject(autoPersObject._GetProxyClass(), persSession);
                for (AutopersObjectField objectField : autoPersObject._GetFields().values()) {
                    AutopersObjectField newField = objectField.clone();
                    AutopersFunction function = objectField.getQueryFunction();
                    if (function != null) {
                        Map<String, Object[]> functions = function.getFunctions();
                        if (functions != null) {
                            for (String method : functions.keySet()) {
                                Object[] args = functions.get(method);
                                if (method.equals("_None")) {
                                    newField.setValue(dataList.get(0)._GetFields().get(objectField.getField()).getValue());
                                    object._PutField(newField);
                                } else if (method.equals("_SubString")) {
                                    newField.setValue(dataList.get(0)._GetFields().get(objectField.getField()).getValue());
                                    object._PutField(newField);
                                } else if (method.equals("_Sum")) {
                                    if (objectField.getFieldClassName().equals(Integer.class.getName())) {
                                        int sum = 0;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                int value = (Integer) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                sum += value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(sum);
                                        object._PutField(newField);
                                    } else if (objectField.getFieldClassName().equals(Double.class.getName())) {
                                        double sum = 0d;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                Double value = (Double) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                sum += value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(sum);
                                        object._PutField(newField);
                                    } else {
                                        newField.setValue(null);
                                        object._PutField(newField);
                                    }

                                } else if (method.equals("_Avg")) {
                                    if (dataList == null || dataList.size() == 0) {
                                        newField.setValue(null);
                                        object._PutField(newField);
                                    } else if (objectField.getFieldClassName().equals(Integer.class.getName())) {
                                        int sum = 0;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                int value = (Integer) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                sum += value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue((int) (sum / dataList.size()));
                                        object._PutField(newField);
                                    } else if (objectField.getFieldClassName().equals(Double.class.getName())) {
                                        double sum = 0d;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                Double value = (Double) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                sum += value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(sum / dataList.size());
                                        object._PutField(newField);
                                    } else {
                                        newField.setValue(null);
                                        object._PutField(newField);
                                    }
                                } else if (method.equals("_Max")) {
                                    if (objectField.getFieldClassName().equals(Integer.class.getName())) {
                                        Integer max = null;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                int value = (Integer) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                if (max == null || max < value) {
                                                    max = value;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(max);
                                        object._PutField(newField);
                                    } else if (objectField.getFieldClassName().equals(Double.class.getName())) {
                                        Double max = null;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                Double value = (Double) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                if (max == null || max < value)
                                                    max = value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(max);
                                        object._PutField(newField);
                                    } else {
                                        String max = null;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                String value = tmpObj._GetFields().get(objectField.getField()).getValue().toString();
                                                if (max == null || max.compareTo(value) > 0)
                                                    max = value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(max);
                                        object._PutField(newField);
                                    }
                                } else if (method.equals("_Min")) {
                                    if (objectField.getFieldClassName().equals(Integer.class.getName())) {
                                        Integer min = null;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                int value = (Integer) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                if (min == null || min > value) {
                                                    min = value;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(min);
                                        object._PutField(newField);
                                    } else if (objectField.getFieldClassName().equals(Double.class.getName())) {
                                        Double min = null;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                Double value = (Double) tmpObj._GetFields().get(objectField.getField()).getValue();
                                                if (min == null || min > value)
                                                    min = value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(min);
                                        object._PutField(newField);
                                    } else {
                                        String min = null;
                                        for (AutopersObject tmpObj : dataList) {
                                            try {
                                                String value = tmpObj._GetFields().get(objectField.getField()).getValue().toString();
                                                if (min == null || min.compareTo(value) < 0)
                                                    min = value;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        newField.setValue(min);
                                        object._PutField(newField);
                                    }
                                } else {

                                }
                            }
                        }
                    }
                }
                result.add(object);

            }


            return result;


        } else {
            //简单查询合并数据
            List<AutopersObject> results = new ArrayList<>();
            for (QueryThead queryThead : queryTheads) {
                List<AutopersObject> result = queryThead.getResult();
                if (result != null)
                    results.addAll(result);
            }

            //处理order by
            for (AutopersQueryMethod method : query.getMethods()) {
                if (method.getMethod().getName().equals("orderBy")) {
                    AutopersObjectField objectField = method.getFields().get(0);
                    String filed = objectField.getField();
                    results.sort((o1, o2) -> {
                        if (o1 == null || o2 == null || o1.equals(o2))
                            return 0;
                        Object v1 = o1._GetFields().get(filed).getValue();
                        Object v2 = o1._GetFields().get(filed).getValue();
                        if (v1 == null || v2 == null || v1.equals(v2))
                            return 0;
                        if (v1.getClass().equals(int.class) || v1.getClass().equals(Integer.class)) {
                            return ((Integer) v1).compareTo((Integer) v2);
                        } else if (v1.getClass().equals(Double.class) || v1.getClass().equals(double.class)) {
                            return ((Double) v1).compareTo((Double) v2);
                        } else {
                            return v1.toString().compareTo(v2.toString());
                        }
                    });

                    break;
                }
            }

            return results;
        }
    }


}

package org.violetime.autopers.session.objects;

import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectsFactory;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.tool.AutopersQueryTool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 分表使用，组合对象查询线程
 */
public class QueryCombineThead extends Thread {
    private final static Logger logger = Logger.getLogger(QueryCombineThead.class.getName());
    private List<AutopersObject> result;
    private Map<String, AutopersMappingClass> mappingClassMap;
    private String codeId;
    private AutopersObject autoPersObject;
    private AutopersQuery query;
    private Map<String, Class<?>> xmlClassMap;
    private boolean isEnd;
    private long start;
    private String tableName;

    private AutopersSession persSession;

    public AutopersSession getPersSession() {
        return persSession;
    }

    public void setPersSession(AutopersSession persSession) {
        this.persSession = persSession;
    }
    private AutopersPlatform platform;

    public AutopersPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(AutopersPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void run() {
        try {
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
                if (tableName.startsWith(mappingClass.getName()))
                    querySql.append(tableName + " " + xmlClass.getSimpleName() + ",");
                else
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

            logger.log(Level.FINE, persSession.getIndex() + " " + querySql.toString());

            //开始执行查询方法
            PreparedStatement preparedStatement = persSession.getPreparedStatement();
            ResultSet resultSet = preparedStatement.executeQuery(querySql.toString());

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
                if (result == null)
                    result = new ArrayList<>();
                result.add(persObject);
            }
            preparedStatement.close();
            persSession.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                persSession.getConnection().close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            try {
                persSession.getConnection().close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        isEnd = true;
    }

    public List<AutopersObject> getResult() {
        return result;
    }

    public void setResult(List<AutopersObject> result) {
        this.result = result;
    }

    public Map<String, AutopersMappingClass> getMappingClassMap() {
        return mappingClassMap;
    }

    public void setMappingClassMap(Map<String, AutopersMappingClass> mappingClassMap) {
        this.mappingClassMap = mappingClassMap;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public AutopersObject getAutoPersObject() {
        return autoPersObject;
    }

    public void setAutoPersObject(AutopersObject autoPersObject) {
        this.autoPersObject = autoPersObject;
    }

    public AutopersQuery getQuery() {
        return query;
    }

    public void setQuery(AutopersQuery query) {
        this.query = query;
    }

    public Map<String, Class<?>> getXmlClassMap() {
        return xmlClassMap;
    }

    public void setXmlClassMap(Map<String, Class<?>> xmlClassMap) {
        this.xmlClassMap = xmlClassMap;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

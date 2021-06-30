package org.violetime.autopers.session.objects;

import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectsFactory;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionPool;
import org.violetime.autopers.session.tool.AutopersQueryTool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * 分表使用，对象查询线程
 */
public class QueryThead extends Thread{
    private final static Logger logger = Logger.getLogger(QueryThead.class.getName());
    private List<AutopersObject> result;
    private AutopersMappingClass mappingClass;
    private String codeId;
    private AutopersObject autoPersObject;
    private AutopersQuery query;
    private Class<?> xmlClass;
    private boolean isEnd;
    private long start;
    private String tableName;
    private AutopersPlatform platform;

    public AutopersPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(AutopersPlatform platform) {
        this.platform = platform;
    }

    private AutopersSession persSession;

    public AutopersSession getPersSession() {
        return persSession;
    }

    public void setPersSession(AutopersSession persSession) {
        this.persSession = persSession;
    }

    @Override
    public void run() {
        try {
            List<IAutopersMappingField> mappingFields = mappingClass.getFields();
            //缓存sql
            String sql = null;
            if (codeId != null && AutopersSessionPool.isProjectModel) {
                if (AutopersQueryTool.getCacheSql(codeId) != null) {
                    sql = AutopersQueryTool.getCacheSql(codeId);
                } else {
                    sql = AutopersQueryTool.getResultSQL(mappingClass, autoPersObject, mappingFields, platform, tableName);
                    AutopersQueryTool.putCacheSql(codeId, sql);
                }
            } else {
                sql = AutopersQueryTool.getResultSQL(mappingClass, autoPersObject, mappingFields, platform, tableName);
            }
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
            logger.info("Q" + persSession.getIndex() + " " + sql);

            PreparedStatement preparedStatement = persSession.getPreparedStatement();
            ResultSet resultSet = preparedStatement.executeQuery(sql);

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
                if (result == null)
                    result = new ArrayList<>();
                result.add(persObject);
            }
            preparedStatement.close();
            logger.info("Q" + persSession.getIndex() + " Total execution " + getResultSize() + " ,time " + (System.currentTimeMillis() - start) + "ms");
            persSession.getConnection().close();
            isEnd = true;
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

    public int getResultSize() {
        if (result == null)
            return 0;
        return result.size();
    }

    public List<AutopersObject> getResult() {
        return result;
    }

    public void setResult(List<AutopersObject> result) {
        this.result = result;
    }

    public AutopersMappingClass getMappingClass() {
        return mappingClass;
    }

    public void setMappingClass(AutopersMappingClass mappingClass) {
        this.mappingClass = mappingClass;
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

    public Class<?> getXmlClass() {
        return xmlClass;
    }

    public void setXmlClass(Class<?> xmlClass) {
        this.xmlClass = xmlClass;
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

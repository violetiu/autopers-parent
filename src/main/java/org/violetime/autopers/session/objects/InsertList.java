package org.violetime.autopers.session.objects;

import org.violetime.autopers.cache.AutopersCache;
import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectsFactory;
import org.violetime.autopers.objects.part.AutopersPart;
import org.violetime.autopers.objects.part.AutopersPartFactory;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.platform.AutopersPlatformObject;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionFactory;
import org.violetime.autopers.session.AutopersSessionPool;
import org.violetime.autopers.session.tool.AutopersPartTool;
import org.violetime.autopers.session.tool.AutopersQueryTool;
import org.violetime.autopers.units.AutopersObjectsUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertList implements AutopersPlatformInvoke {
    private final static Logger logger = Logger.getLogger("Autopers");
    private AutopersSession autoPersSession;
    private AutopersPlatformObject platformObject;
    private Object[] args;
    private String throwablePrint;
    private DataBaseSource baseSource;

    @Override
    public Object invoke() throws Throwable {
        // TODO Auto-generated method stub
        List<AutopersObject> objects = (List<AutopersObject>) args[0];
        if (objects == null || objects.size() == 0)
            return 0l;
        long start=System.currentTimeMillis();
        logger.log(Level.FINE,"Preparing to insert");
        AutopersObject autoPersObject = (AutopersObject) objects.get(0);
        Map<String, Class<?>> xmlClassMap = autoPersObject._GetMappingClass();
        Map<String, AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();
        String codeId = null;
        if (autoPersSession.codeId() != null) {
            codeId = autoPersSession.codeId();
        } else {
            codeId = AutopersQueryTool.getCodeIdFromObjectStatic(3);
        }
        if (!autoPersObject._IsCombine()) {
            Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
            AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
            //System.out.println("InsertList 加载实体类映射类  "+mappingClass.getClassPath());
            HashMap<String, IAutopersMappingField> colnumMap = null;
            if (AutopersSessionPool.isProjectModel) {
                //生产模式启用缓存
                Object cache = AutopersCache.peek(codeId);
                if (cache != null) {
                    colnumMap = (HashMap<String, IAutopersMappingField>) cache;
                } else {
                    colnumMap = getSQLColumn(autoPersObject, mappingClass);
                    AutopersCache.push(codeId, colnumMap);
                }
            } else {
                colnumMap = getSQLColumn(autoPersObject, mappingClass);
            }
            //按需要查询的表进行分组
            Map<String,List<AutopersObject>> objectMap=new HashMap<>();
            for (AutopersObject obj : objects) {
                if (obj._GetFields() == null)
                    continue;
                String tableName= AutopersPartTool.getPartTable(mappingClass,autoPersObject,obj,baseSource);
                List<AutopersObject> objectList= objectMap.get(tableName);
                if(objectList==null){
                    objectList=new ArrayList<>();
                    objectMap.put(tableName,objectList);
                }
                objectList.add(obj);
            }
            String platformPropertyInsert = platformObject.getPropertys().get("insert");
            String platformPropertyItem = platformObject.getPropertys().get("item");
            String platformPropertySelect = platformObject.getPropertys().get("select");
            String platformPropertycount = platformObject.getPropertys().get("count");
            StringBuffer colnumSQl = new StringBuffer();
            for (Object key : colnumMap.keySet()) {
                IAutopersMappingField field = colnumMap.get(key);
                colnumSQl.append( ",").append( field.getColumn());
            }

            StringBuffer colnumSQls = new StringBuffer();
            colnumSQls.append("(" ).append(colnumSQl.substring(1)) .append( ")");

            Connection connection = autoPersSession.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(" ");
            //按照分组结果 分批插入
            long count = 0;
            for(String tableName:objectMap.keySet()){
                List<AutopersObject> objectList=objectMap.get(tableName);
                count+=insertTable(objectList,platformPropertycount,platformPropertyInsert,platformPropertyItem,platformPropertySelect,preparedStatement,tableName,colnumSQls,colnumMap,mappingClass);
            }
            logger.log(Level.FINE,"Start inserting..."+(System.currentTimeMillis()-start)+"ms");

            long[] counts = preparedStatement.executeLargeBatch();
            for (long num : counts) {
                count += num;
            }
            logger.log(Level.FINE,"Insert data is complete : " + count+"n,"+(System.currentTimeMillis()-start)+"ms");
            preparedStatement.close();
            connection.commit();
            return count;
        } else {
            return 0l;
        }

    }

    private  int insertTable(List<AutopersObject> objectList,String platformPropertycount,String platformPropertyInsert,String platformPropertyItem,String  platformPropertySelect,PreparedStatement preparedStatement,String tableName,StringBuffer colnumSQls, HashMap<String, IAutopersMappingField> colnumMap ,AutopersMappingClass  xmlClass) throws SQLException {
        int maxSize = 1000;
        if (platformPropertycount != null) {
            try {
                maxSize = Integer.parseInt(platformPropertycount);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        int rs = objectList.size() / maxSize;
        int remainder = objectList.size() % maxSize;
        int number = 0;
        if (remainder == 0) {
            number = rs;
        } else {
            number = rs + 1;
        }

        StringBuffer itemValues = new StringBuffer();
        StringBuffer itemValue = new StringBuffer();
        StringBuffer sql =new StringBuffer();
        for (int index = 0; index < number; index++) {
            int lastIndex = maxSize * (1 + index);
            if (lastIndex > objectList.size())
                lastIndex = objectList.size();
            List<AutopersObject> subObjects = objectList.subList(index * maxSize, lastIndex);
            if (subObjects == null || subObjects.size() == 0)
                continue;
            if(sql.length()>0)
                sql.delete(0,sql.length());
            sql.append(platformPropertyInsert.replace("{table}", tableName).replace("{colnum}", colnumSQls));
            for(AutopersObject subObj:subObjects){
                //拼接sql
                if (subObj._GetFields() == null)
                    continue;
                HashMap<String, String> valuesMap = new HashMap<String, String>();
                for (Object key : colnumMap.keySet()) {
                    IAutopersMappingField field = colnumMap.get(key);
                    AutopersObjectField objectField = subObj._GetFields().get(field.getName());
                    String val = null;
                    if (objectField != null && objectField.getValue() != null) {
                        val = AutopersObjectsUnit.dealSqlValue(objectField.getValue(), field);
                    }
                    if (val != null) {
                        valuesMap.put(key.toString(), val);
                    } else {
                        if (field.getGenerator() != null) {
                            val = field.getGenerator().getValue();
                            try {
//                                AutopersObjectField generatorField = AutopersObjectsFactory.newInstanceField(val,
//                                        field.getName(), xmlClass.getName());
//                                object._GetFields().put(field.getName(), generatorField);
                                valuesMap.put(key.toString(), "'" + val + "'");
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else {
                            //  throw new Exception("对象属性中存在NULL,位置在-->" + xmlClass + "." + key);
                            logger.log(Level.FINE,"对象属性中存在NULL,位置在-->" + xmlClass + "." + key);
                        }
                    }
                }
                //map
                if(itemValue.length()>0)
                    itemValue.delete(0,itemValue.length());
                for (Object key : colnumMap.keySet()) {
                    itemValue.append(",") .append( valuesMap.get(key));
                }
                if(itemValues.length()>0)
                    itemValues.delete(0,itemValues.length());
                itemValues .append("(" ).append( itemValue.substring(1)).append( ")");
                sql.append( platformPropertyItem.replace("{table}", tableName).replace("{colnum}", colnumSQls).replace("{values}", itemValues)).append( ",");
            }
            subObjects=null;
            sql.append(platformPropertySelect);
            if (sql.charAt(sql.length()-1)==',')
                sql.deleteCharAt(sql.length()-1);
            preparedStatement.addBatch(sql.toString());
        }
        itemValue=null;
        itemValues=null;
        sql=null;
        return  objectList.size();
    }

    private HashMap<String, IAutopersMappingField> getSQLColumn(AutopersObject autoPersObject, AutopersMappingClass mappingClass) throws Exception {
        HashMap<String, IAutopersMappingField> colnumMap = new HashMap<String, IAutopersMappingField>();
        for (IAutopersMappingField mappingField : mappingClass.getFields()) {
            if (autoPersObject._GetFields() == null)
                continue;
            String value = null;
            AutopersObjectField autoPersObjectField = autoPersObject._GetFields().get(mappingField.getName());
            if (autoPersObjectField == null || autoPersObjectField.getValue() == null)
                value = null;
            else
                value = AutopersObjectsUnit.dealSqlValue(autoPersObjectField.getValue(), mappingField);
            if (value != null) {
                //System.out.println("InsertList 检测到键  "+mappingField.getName());
                colnumMap.put(mappingField.getName(), mappingField);
            } else if (value == null && mappingField.getGenerator() != null) {
                value = mappingField.getGenerator().getValue();
//                System.out.println("InsertList 检测到主键  " + mappingField.getName());
                try {
                    colnumMap.put(mappingField.getName(), mappingField);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (colnumMap == null || colnumMap.size() == 0) {
            throw new Exception("没有发现对象要插入的列集合。");
        }
        return colnumMap;
    }

    @Override
    public String throwablePrint() {
        // TODO Auto-generated method stub
        return throwablePrint;
    }


}

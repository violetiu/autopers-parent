package org.violetime.autopers.session.tool;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.database.DataBaseTable;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.part.AutopersPart;
import org.violetime.autopers.objects.part.AutopersPartFactory;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 分表 工具
 */
public class AutopersPartTool {
    private final static Logger logger = Logger.getLogger(AutopersPartTool.class.getName());


    /**
     * 获取分区表名
     *
     * @param mappingClass
     * @param template
     * @param object
     * @param baseSource
     * @return
     */
    public static String getPartTable(AutopersMappingClass mappingClass, AutopersObject template, AutopersObject object, DataBaseSource baseSource) {
        String tableName = mappingClass.getName();
        String partKey = AutopersPartFactory.key(template._GetProxyClass().getSimpleName(), object);
        if (partKey != null) {
            tableName += "$" + partKey;
            createNewPartTable(baseSource,tableName,mappingClass);
        }
        return tableName;
    }

    private static synchronized void createNewPartTable(DataBaseSource baseSource, String tableName,AutopersMappingClass mappingClass) {
        //判断是否需要新建表
        if (!DataBaseFactory.hasTable(baseSource, tableName)) {
            AutopersSession persSession = AutopersSessionFactory.openSession(baseSource);
            //创建新表,并完成加载
            persSession.copyTableStructure(tableName, mappingClass.getName());
            DataBaseTable dataBaseTable = new DataBaseTable();
            dataBaseTable.setTableName(tableName);
            baseSource.getTables().add(dataBaseTable);
        }
    }




    /**
     * 获取查询时需要的表名
     *
     * @param baseSource
     * @param tableName
     * @param part
     * @param query
     * @return 如果为空，就是用原本的表名
     */
    public static List<String> getPartTables(DataBaseSource baseSource, String tableName, AutopersPart part, AutopersQuery query) {
        List<String> tables = DataBaseFactory.getTableParts(baseSource, tableName);
        if (tables == null || tables.size() == 0) {
            // logger.log(Level.WARNING,"tables size is  null or 0 ,name is "+tableName+","+part.getClass().getName()+","+query.getMethods().size());
            return null;
        }
        List<String> result = new ArrayList<>();
        for (String table : tables) {
            if (table.split("\\$").length > 1) {
                boolean isPart = part.query(table.split("\\$")[1], query);
                if (isPart) {
                    if (!result.contains(table))
                        result.add(table);
                }
            } else {
                if (!result.contains(table))
                    result.add(table);
            }

        }
        return result;
    }
}

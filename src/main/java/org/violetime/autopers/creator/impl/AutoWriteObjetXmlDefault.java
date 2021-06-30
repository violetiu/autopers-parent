package org.violetime.autopers.creator.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import java.util.logging.Logger;

import org.violetime.autopers.creator.AutoWriteObjectXml;
import org.violetime.autopers.database.DataBaseColumn;
import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.database.DataBaseTable;
import org.violetime.autopers.mapping.AutopersMapping;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionFactory;

/**
 * 自动写入数据库实体类和xml实体配置文件
 *
 * @author taoyo
 */
public class AutoWriteObjetXmlDefault implements AutoWriteObjectXml {
    private final static Logger logger = Logger.getLogger(AutoWriteObjetXmlDefault.class.getName());
    private HashMap<String, String> dataTypeMap;
    private String appPath;
    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }
    public void setDataTypeMap(HashMap<String, String> dataTypeMap) {
        this.dataTypeMap = dataTypeMap;
    }
    public AutoWriteObjetXmlDefault() {
        this.isCreator = false;
    }
    private String classPackage = null;
    private void findClassPackage(String folder) throws Exception {
        if (classPackage != null)
            return;
        File file = new File(folder);
        if (file.exists()) {
            if (file.isDirectory()) {
                for (String temp : file.list()) {
                    findClassPackage(file.getPath() + "/" + temp);
                }
            } else if (file.isFile() && file.getName().endsWith(".java")) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = bufferedReader.readLine();
                if (line != null && line.startsWith("package ")) {
                    String temp = line.replace("package ", "").replace(";", "");
                    if (temp.contains("."))
                        temp = temp.split("\\.")[0];
                    String tempPath = objects.replace("/", ".");
                    int index = tempPath.indexOf(temp);
                    tempPath = tempPath.substring(index);
                    classPackage = tempPath;
                }
                bufferedReader.close();
                fileReader.close();
            }
        } else {
            System.out.println("NO F " + folder);
        }
    }
    @Override
    public String autoWrite() {
        // TODO Auto-generated method stub
        try {
            findClassPackage(appPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Object key : DataBaseFactory.getDataBaseSourceMap().keySet()) {
            DataBaseSource baseSource = DataBaseFactory.getDataBaseSourceMap()
                    .get(key);
            if (this.database.equals("*")) {
                autoWriteDatabaseSource(baseSource);
            } else if (this.database.equals(key.toString())) {
                autoWriteDatabaseSource(baseSource);
                break;
            } else {

            }
        }
        return null;
    }
    private void autoWriteDatabaseSource(DataBaseSource baseSource) {
        AutopersSession persSession = AutopersSessionFactory
                .openSession(baseSource);
        List<DataBaseTable> baseTables = persSession.getTables();
        for (int index=0;index<baseTables.size();index++) {
            DataBaseTable baseTable= baseTables.get(index);
            double persent=Math.floor((index+1)*1.0/baseTables.size()*100);
            //不创建 分表
            if(baseTable.getTableName().contains("$"))
                continue;
            //创建正表
            if (this.tables.equals("*")) {
                autoWritDataBaseTable(baseTable, persSession,baseSource.getName(),persent);
            } else if (this.tables.equals("#")) {
                AutopersMappingClass mappingClass = AutopersMapping
                        .getMappingClassXml(baseTable.getTableName());
                if (mappingClass != null) {
                    if (mappingClass.getUpdatetime().equals(
                            baseTable.getUpdateTime())) {
                        continue;
                    }
                }
                autoWritDataBaseTable(baseTable, persSession,baseSource.getName(),persent);
            } else if (this.tables.length() > 0) {
                if (this.tables.equals(baseTable.getTableName())) {
                    autoWritDataBaseTable(baseTable, persSession,baseSource.getName(),persent);
                    break;
                }
            } else {

            }
        }
        persSession.close();
    }

    private void autoWritDataBaseTable(DataBaseTable baseTable,
                                       AutopersSession autoPersSession,String baseSource,double persent) {
        List<DataBaseColumn> baseColumns = autoPersSession.getColumns(baseTable
                .getTableName());
        if (baseColumns == null) {
            System.out.println("baseColumns is null");
            return;
        }
        AutoWriteObjectDefault.setDataTypeMap(dataTypeMap);
        AutoWriteXmlDefault.setDataTypeMap(dataTypeMap);
        String result;
        try {
            String objectPathAll;
            String xmlPathAll;
            String startPath = this.getClass().getClassLoader().getResource("").getPath();
            if (isCreator) {
                objectPathAll = appPath + "/" + objects;
                xmlPathAll = appPath + "/" + mappingPath;
            } else {
                objectPathAll = appPath +"/"+ objects;
                xmlPathAll = appPath  +"/"+ mappingPath;
            }
            logger.info("Creation complete --> "+persent+"% " + baseTable.getTableName()+" : "+baseTable.getComment());

            if (classPackage != null) {
                AutoWriteObjectDefault.autoWrite(baseColumns, baseTable,
                        objectPathAll, classPackage,baseSource);
                AutoWriteXmlDefault.autoWrite(baseColumns, baseTable, xmlPathAll, classPackage,baseSource);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private String objects;
    public String getObjects() {
        return objects;
    }
    public void setObjects(String objects) {
        this.objects = objects;
    }
    public String getMapping() {
        return mapping;
    }
    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
    private String mapping;
    private String isAuto;
    private String tables;
    private String database;
    private String workspace;
    public String getWorkspace() {
        return workspace;
    }
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
    }
    public String getTables() {
        return tables;
    }
    public void setTables(String tables) {
        this.tables = tables;
    }
    public String getObjectPath() {
        String objectPathAll = appPath + "WEB-INF/classes/" + objects;
        return objectPathAll;
    }
    public void setObjectPath(String objectPath) {
        this.objects = objectPath;
    }
    private String mappingPath;
    private String configPath;
    @Override
    public void setMappingPath(String mappingPath) {
        this.mappingPath = mappingPath;
    }
    @Override
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    private boolean isCreator;
    @Override
    public void isCreator() {
        this.isCreator = true;
    }
    public String getXmlPath() {
        String xmlPathAll = appPath +"/" + mapping;
        return xmlPathAll;
    }
    @Override
    public String getMappingPath() {
        return this.mapping;
    }
    public void setXmlPath(String xmlPath) {
        this.mapping = xmlPath;
    }
    public String getIsAuto() {
        return isAuto;
    }
    public void setIsAuto(String isAuto) {
        this.isAuto = isAuto;
    }
}

package org.violetime.autopers.units;

import org.violetime.autopers.context.LoadContext;
import org.violetime.autopers.context.LoadDataType;
import org.violetime.autopers.context.LoadObjectXml;
import org.violetime.autopers.context.LoadPlatform;
import org.violetime.autopers.creator.AutoWriteFactory;
import org.violetime.autopers.database.DataBaseFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * 依据数据库表 写入实体类对象和映射文件；
 * 需要设置四个属性：
 * webApp：项目开发位置 /workspaces/autopers/
 * objectPath:对象写入路径 src/main/java/com/autopers/objects/
 * mappingPath:映射文件写入路径 src/main/resources/mapping/
 * configPath:启动必须配置文件路径。src/main/webapp/WEB-INF/
 * 调用create方法执行写入过程。
 */
public class AutopersStart implements IAutopersObjectCreator{
    private static final Logger logger = Logger.getLogger("Autopers");

    /**
     * 写入实体类对象和映射文件；
     */
    public void start() {
        logger.info("--------------Autopers Loadding-------------------");
        String autopersContextLocation = webApp + "/" + configPath + "/autopersContext.xml";
        String dataTypeConfigLocation = webApp + "/" + configPath + "/autopersDataType.xml";
        String platformConfigLocation = webApp + "/" + configPath + "/autopersPlatform.xml";
        String mappingLocation = webApp + "/" + mappingPath+"/";
        try {
            LoadPlatform platform = new LoadPlatform();
            platform.load(new FileInputStream(platformConfigLocation));
            LoadDataType loadDataType = new LoadDataType();
            HashMap<String, String> dataTypeMap = loadDataType.load(new FileInputStream(dataTypeConfigLocation));
            LoadContext context = new LoadContext();
            context.load(new FileInputStream(autopersContextLocation));
            AutoWriteFactory.getAutoWriteObjectXml().setDataTypeMap(dataTypeMap);
            DataBaseFactory.initDataBaseSourceTables();
            logger.info("--------------Autopers Bean-------------------");
            List<InputStream> mappings=new ArrayList<>();
            DataBaseFactory.getDataBaseSourceMap().forEach((key,surce)->{
                surce.getTables().forEach((table)->{
                    if(table.getTableName().split("\\$").length<2){
                            try {
                                InputStream is =new FileInputStream(mappingLocation+AutopersCodeName.className(table.getTableName())+".xml");
                                mappings.add(is);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                });
            });
            LoadObjectXml objectXml = new LoadObjectXml();
            objectXml.autoLoad(mappings);
            logger.info("--------------Autopers Loaded-------------------");
        }catch (Exception e){
        e.printStackTrace();
        }

    }
    private String webApp;
    public String getWebApp() {
        return webApp;
    }
    public void setWebApp(String webApp) {
        this.webApp = webApp;
    }
    public String getObjectPath() {
        return objectPath;
    }
    public void setObjectPath(String objectPath) {
        this.objectPath = objectPath;
    }
    public String getMappingPath() {
        return mappingPath;
    }
    public void setMappingPath(String mappingPath) {
        this.mappingPath = mappingPath;
    }
    public String getConfigPath() {
        return configPath;
    }
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public void create() {

    }

    private String objectPath;
    private String mappingPath;
    private String configPath;
}

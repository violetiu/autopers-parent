package org.violetime.autopers.units;

import org.violetime.autopers.creator.AutoWriteFactory;
import org.violetime.autopers.context.LoadContext;
import org.violetime.autopers.context.LoadDataType;
import org.violetime.autopers.context.LoadPlatform;

import java.io.FileInputStream;
import java.util.logging.Logger;

import java.io.File;
import java.util.HashMap;

/**
 * 依据数据库表 写入实体类对象和映射文件；
 * 需要设置四个属性：
 * webApp：项目开发位置 /workspaces/autopers/
 * objectPath:对象写入路径 src/main/java/com/autopers/objects/
 * mappingPath:映射文件写入路径 src/main/resources/mapping/
 * configPath:启动必须配置文件路径。src/main/webapp/WEB-INF/
 * 调用create方法执行写入过程。
 */
public class AutopersObjectCreator implements IAutopersObjectCreator {
    private static final Logger logger = Logger.getLogger("Autopers");

    public static void main(String[] args) {
        if (args != null) {
            AutopersObjectCreator creator = new AutopersObjectCreator();
            if (args[0] != null && args[2].length() > 0)
                creator.setWebApp(args[0]);
            else
                return;
            if (args[1] != null && args[2].length() > 0)
                creator.setConfigPath(args[1]);
            else
                creator.setConfigPath("src/main/webapp/WEB-INF");

            if (args[2] != null && args[2].length() > 0)
                creator.setObjectPath(args[2]);
            else {
                File file = new File(args[0]);
                creator.setObjectPath("src/main/java/com/" + file.getName() + "/objects");
            }
            if (args[3] != null && args[2].length() > 0)
                creator.setMappingPath(args[3]);
            else
                creator.setMappingPath("src/main/resources/mapping");
            creator.create();
        }
    }
    /**
     * 写入实体类对象和映射文件；
     */
    public void create() {
        logger.info("--------------Autopers Loadding-------------------");

        String autopersContextLocation = webApp + "/" + configPath + "/autopersContext.xml";
        String dataTypeConfigLocation = webApp + "/" + configPath + "/autopersDataType.xml";
        String platformConfigLocation = webApp + "/" + configPath + "/autopersPlatform.xml";
        try {
            LoadPlatform platform = new LoadPlatform();
            platform.load(new FileInputStream(platformConfigLocation));
            LoadDataType loadDataType = new LoadDataType();
            HashMap<String, String> dataTypeMap = loadDataType.load(new FileInputStream(dataTypeConfigLocation));

            LoadContext context = new LoadContext();
            context.load(new FileInputStream(autopersContextLocation));

            AutoWriteFactory.getAutoWriteObjectXml().setDataTypeMap(dataTypeMap);
            AutoWriteFactory.creator(this);
            AutoWriteFactory.write();

            logger.info("--------------Autopers Loaded-------------------");
        }catch (Exception e){

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
    private String objectPath;
    private String mappingPath;
    private String configPath;
}

package org.violetime.autopers.creator;

import org.violetime.autopers.units.AutopersObjectCreator;
import org.violetime.autopers.units.IAutopersObjectCreator;

import java.util.logging.Logger;

/**
 * 自动写入数据库实体类对象和xml配置文件
 *
 * @author taoyo
 */
public class AutoWriteFactory {
    private static final Logger logger = Logger.getLogger("Autopers");
    private static AutoWriteObjectXml autoWriteObjectXml;
    /**
     * 设置写入用接口对象
     *
     * @param autoWriteObjectXml
     */
    public static void setAutoWriteObjectXml(AutoWriteObjectXml autoWriteObjectXml) {
        AutoWriteFactory.autoWriteObjectXml = autoWriteObjectXml;
    }

    /**
     * 获取自动写入对象
     * @return
     */
    public static AutoWriteObjectXml getAutoWriteObjectXml() {
        return autoWriteObjectXml;
    }
    /**
     * 开始自动写入；
     * 判断是否为手动写入对象还是自动写入对象，并执行不同的方法。
     * 调用autoWriteObjectXml的autoWrite方法。
     */
    public static void autoWrite() {
        if (autoWriteObjectXml != null && autoWriteObjectXml.getIsAuto() != null && autoWriteObjectXml.getIsAuto().equals("true")) {
            logger.info("AutoWriteFactory autoWrite ....");
            if (CREATOR != null) {
                autoWriteObjectXml.isCreator();
                autoWriteObjectXml.setAppPath(CREATOR.getWebApp());
                autoWriteObjectXml.setConfigPath(CREATOR.getConfigPath());
                autoWriteObjectXml.setObjectPath(CREATOR.getObjectPath());
                autoWriteObjectXml.setMappingPath(CREATOR.getMappingPath());
            }
            autoWriteObjectXml.autoWrite();
        }
    }
    /**
     * 开始写入；
     * 调用autoWriteObjectXml的autoWrite方法。
     */
    public static void write() {
        if (autoWriteObjectXml != null) {
            logger.info("AutoWriteFactory write ....");
            if (CREATOR != null) {
                autoWriteObjectXml.isCreator();
                autoWriteObjectXml.setAppPath(CREATOR.getWebApp());
                autoWriteObjectXml.setConfigPath(CREATOR.getConfigPath());
                autoWriteObjectXml.setObjectPath(CREATOR.getObjectPath());
                autoWriteObjectXml.setMappingPath(CREATOR.getMappingPath());
            }
            autoWriteObjectXml.autoWrite();
        }
    }
    private static IAutopersObjectCreator CREATOR;

    /**
     * 设置手动写入类
     * @param creator
     */
    public static void creator(IAutopersObjectCreator creator) {
        CREATOR = creator;

    }

}

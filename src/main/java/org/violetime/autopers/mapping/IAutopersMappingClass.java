package org.violetime.autopers.mapping;

import java.util.List;

public interface IAutopersMappingClass {
    /**
     * 获取更新时间
     * @return
     */
    public String getUpdatetime();

    /**
     * 获取表列
     * @return
     */
    public List<IAutopersMappingField> getFields();

    /**
     * 获取表名
     * @return
     */
    public String getName();

    /**
     * 获取表标签
     * @return
     */
    public String getLabel();

    /**
     * 获取表备注
     * @return
     */
    public String getConmment();
    public String getSource();

}

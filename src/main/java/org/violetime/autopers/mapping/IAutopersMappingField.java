package org.violetime.autopers.mapping;

import org.violetime.autopers.generator.Generator;

public interface IAutopersMappingField {

    public Generator getGenerator() ;

    /**
     * 获取键
     * @return
     */
    public String getPrimary();
    public AutopersMappingGenerator getMappingGenerator() ;
    public AutopersMappingForeign getMappingForeign();

    /**
     * 获取对象属性名
     * @return
     */
    public String getName();

    /**
     * 获取表列名
     * @return
     */
    public String getColumn();
    public String getJdbctype();
    public String getLabel() ;
    public String getJavatype();
    public String getComment();


}

package org.violetime.autopers.query.objects;

import java.util.List;
import java.util.Set;

import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.query.QueryObjectReturnData;
import org.violetime.autopers.session.tool.AutopersQueryTool;
import org.violetime.autopers.units.AutopersObjectsUnit;

public class Equals implements AutopersPlatformInvoke {
    private List<AutopersObjectField> fieldList;
    private Object[] args;
    private String className;
    private AutopersObjectField combineField;
    private AutopersFunction function;
    private AutopersPlatform platform;
    protected Set<IAutopersMappingField> mappingFields;
    private AutopersMappingClass mappingClass;
    @Override
    public Object invoke() throws Throwable {
        // TODO Auto-generated method stub
        if (fieldList != null && fieldList.size() > 0 && args != null && args.length > 0) {
            IAutopersMappingField field =AutopersObjectsUnit.getMappingField(fieldList.get(0).getField(),mappingClass,mappingFields);
            if (combineField != null) {
                //处理组合类查询
                if (combineField.getClassName() != null && combineField.getField() != null) {
                    String combineSql = combineField.getClassName().substring(combineField.getClassName().lastIndexOf("\\.") + 1) + "." + combineField.getField();
                  if(function!=null){
                      return function.getSQl(className + "." + field.getColumn(),platform) + "=" + combineSql;
                  }
                   return  className + "." + field.getColumn() + "=" + combineSql;
                }
            }
            if (args[1] == null) {
                return  className + "." + field.getColumn() + " is null ";
            } else {
                String val = AutopersObjectsUnit.getSqlValueByField(field,args[1]);

                if(function!=null){
                    return function.getSQl(className + "." + field.getColumn(),platform) + "=" + val + "";
                   // return new QueryObjectReturnData(function.getSQl(className + "." + getMappingField(fieldList.get(0).getField()).getColumn(),platform) + "=?",args[1]);
                }
                //return new QueryObjectReturnData(className + "." + getMappingField(fieldList.get(0).getField()).getColumn() + "=?",args[1]);
                return className + "." + field.getColumn() + "=" + val + "";
            }

        } else {
            System.out.println(fieldList);
            System.out.println(args);
            return null;
        }
    }

    @Override
    public String throwablePrint() {
        // TODO Auto-generated method stub
        return null;
    }




}

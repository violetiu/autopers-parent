package org.violetime.autopers.session.objects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.violetime.autopers.mapping.IAutopersMappingField;
import org.violetime.autopers.objects.combine.CombineDictionaryObject;
import org.violetime.autopers.platform.AutopersPlatform;
import org.violetime.autopers.session.tool.AutopersQueryTool;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.violetime.autopers.mapping.AutopersMapping;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectJoin;
import org.violetime.autopers.objects.AutopersObjectsFactory;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.units.AutopersObjectsUnit;
/**
 * 对象查询，根据对象的属性进行查询，不支持null
 * @author taoyo
 *
 */
public class QueryObject implements AutopersPlatformInvoke{
	private final static Logger logger=Logger.getLogger(QueryObject.class.getName());
    private  AutopersSession persSession;
    private  Object[] args;
    private AutopersPlatform platform;

	@Override
    public Object invoke()
            throws Throwable {
             
                AutopersObject autoPersObject=(AutopersObject)args[0];
                try{

                    Map<String,Class<?>> xmlClassMap= autoPersObject._GetMappingClass();
                    Map<String,AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();

                    if(!autoPersObject._IsCombine()) {

                        if(AutopersQueryTool.hasResultFunction(autoPersObject)){
                            throw new SQLException("The result funcation can't use in the no group query!");
                        }

                        Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
                        AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
                        if (mappingClass != null) {
                            List<IAutopersMappingField>	 mappingFields=	mappingClass.getFields();
                            if(mappingFields==null||mappingFields.size()==0){
                                logger.log(Level.FINE,"没有获取到mappingFields");
                            }else{
                                StringBuffer primarySql=new StringBuffer();
                                StringBuffer otherSql=new StringBuffer();
                                for(IAutopersMappingField  mappingField:mappingFields){

                                    Object fieldVal=null;
                                    if(autoPersObject._GetFields()!=null&&autoPersObject._GetFields().get(mappingField.getName())!=null)
                                        fieldVal=autoPersObject._GetFields().get(mappingField.getName()).getValue();
                                    if(fieldVal==null)
                                    {
                                        continue;
                                    }
                                    String sqlVal=AutopersObjectsUnit.getSqlValueByField(mappingField, fieldVal);
                                    mappingField.getJavatype();
                                    if(mappingField.getPrimary()!=null&&mappingField.getPrimary().length()>0){
                                        //  logger.log(Level.FINE,"存在主键数据，进行主键查询："+mappingField.getColumn());
                                        primarySql.append(" and "+autoPersObject._GetProxyClass().getSimpleName()+"."+mappingField.getColumn()+"="+sqlVal);

                                    }else{
                                        if(sqlVal.length()==0)
                                        {
                                            otherSql.append(" and "+autoPersObject._GetProxyClass().getSimpleName()+"."+mappingField.getColumn()+" is null");
                                        }else{
                                            otherSql.append(" and "+autoPersObject._GetProxyClass().getSimpleName()+"."+mappingField.getColumn()+"="+sqlVal);
                                        }
                                    }
                                }


                                String sql = AutopersQueryTool.getResultSQL(mappingClass,autoPersObject,mappingFields,platform);


                                if(primarySql.length()>0){
                                    sql+=" where  "+primarySql.substring(4).toString();
                                }else if(otherSql.length()>0){
                                    sql+=" where  "+otherSql.substring(4).toString();
                                }

                                if (autoPersObject._Page() != null) {
                                    sql=autoPersObject._Page().getSql(sql,platform);
                                }

                                logger.log(Level.FINE,sql);
                                throwablePrint=sql;
                                PreparedStatement  preparedStatement= persSession.getPreparedStatement();
                                ResultSet resultSet=preparedStatement.executeQuery(sql);
                                List<AutopersObject> results=new ArrayList<>();
                                while(resultSet.next()){
                                    AutopersObject persObject=(AutopersObject) AutopersObjectsFactory.newInstanceObject(autoPersObject._GetProxyClass());
                                    HashMap<String, AutopersObjectField> filedMap= persObject._GetFields();
                                    for(IAutopersMappingField mappingField:mappingFields){
                                        try{
                                            Object sqlVal = resultSet.getObject(mappingField.getColumn());
                                            if (sqlVal != null) {
                                                AutopersObjectField objectField = AutopersObjectsFactory.newInstanceField(sqlVal,
                                                        mappingField.getName(),xmlClass.getName());
                                                filedMap.put(mappingField.getName(), objectField);
                                            }
                                        }catch (Exception e){

                                        }
                                    }
                                    results.add(persObject);
                                }
                                preparedStatement.close();
                                return results;
                            }

                        }
                        else{
                            logger.log(Level.FINE,"缺少实体类的配置文件");
                        }



                    }else{
                        //组合类对象查询








                    }

        }catch(Exception e){
            e.printStackTrace();

        }

        return null;

    }

	private String throwablePrint;
	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return throwablePrint;
	}
}
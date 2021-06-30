package org.violetime.autopers.session.objects;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.violetime.autopers.mapping.IAutopersMappingField;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.violetime.autopers.mapping.AutopersMapping;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.objects.AutopersObjectField;
import org.violetime.autopers.objects.AutopersObjectJoin;
import org.violetime.autopers.platform.AutopersPlatformInvoke;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.units.AutopersObjectsUnit;

public class QueryObjectCount implements AutopersPlatformInvoke{
    private final static Logger logger=Logger.getLogger(QueryObjectCount.class.getName());
    private  AutopersSession persSession;
    private  Object[] args;

	@Override
    public Object invoke()
            throws Throwable {
             
                AutopersObject autoPersObject=(AutopersObject)args[0];
                try{

                    Map<String,Class<?>> xmlClassMap= autoPersObject._GetMappingClass();
                    Map<String,AutopersMappingClass> mappingClassMap = autoPersObject._GetMapping();

                    if(!autoPersObject._IsCombine()) {
                        Class<?> xmlClass = (Class<?>) xmlClassMap.values().toArray()[0];
                        AutopersMappingClass mappingClass = (AutopersMappingClass) mappingClassMap.values().toArray()[0];
                        if (mappingClass != null) {
                            List<IAutopersMappingField>	 mappingFields=	mappingClass.getFields();
                            if(mappingFields==null||mappingFields.size()==0){
                                logger.log(Level.FINE,"没有获取到mappingFields");
                            }else{
                                StringBuffer primarySql=new StringBuffer();
                                StringBuffer otherSql=new StringBuffer();
                                for(IAutopersMappingField mappingField:mappingFields){

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
                                        logger.log(Level.FINE,"存在主键数据，进行主键查询："+mappingField.getColumn());
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
                                String sql="select count(*) from "+mappingClass.getName()+" "+autoPersObject._GetProxyClass().getSimpleName();
                                if(primarySql.length()>0){
                                    sql+=" where  "+primarySql.substring(4).toString();
                                }else if(otherSql.length()>0){
                                    sql+=" where  "+otherSql.substring(4).toString();
                                }

                                logger.log(Level.FINE,sql);
                                PreparedStatement  preparedStatement= persSession.getPreparedStatement();

                                ResultSet resultSet=preparedStatement.executeQuery(sql);
                                int results=0;
                                if(resultSet.next()){
                                    results=resultSet.getInt(1);
                                }
                                return results;
                            }

                        }
                        else{
                            logger.log(Level.FINE,"缺少实体类的配置文件");
                        }


                    }else{



                    }

        }catch(Exception e){
            e.printStackTrace();

        }

        return null;

    }

	@Override
	public String throwablePrint() {
		// TODO Auto-generated method stub
		return null;
	}
}
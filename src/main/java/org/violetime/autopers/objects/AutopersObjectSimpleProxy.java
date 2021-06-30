package org.violetime.autopers.objects;

import org.violetime.autopers.annotation.AutopersAnnotation;
import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.function.AutopersFunctionFactory;
import org.violetime.autopers.mapping.AutopersMapping;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.objects.impl.AutopersObjectFieldDefault;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.query.AutopersQueryFactory;
import org.violetime.autopers.query.AutopersQueryPage;
import org.violetime.autopers.query.impl.AutopersQueryPageDefault;
import org.violetime.autopers.reflect.AutopersReflectClass;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionFactory;
import org.violetime.autopers.session.tool.AutopersQueryTool;
import org.violetime.autopers.units.AutopersObjectsUnit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * 数据库实体接口代理类
 *
 * @author Administrator
 */
public class AutopersObjectSimpleProxy implements InvocationHandler {
    private final static Logger logger = Logger.getLogger("AutopersObjectSimpleProxy");
    private Class<?> object;//代理对象接口
    private AutopersObject persObject;//代理对象

    private HashMap<String, AutopersObjectField> fields;//代理对象属性集合

    private String id;

    /**
     * 对象代理类构造函数
     *
     * @param object 代理接口-对象接口
     */
    public AutopersObjectSimpleProxy(Class<?> object) {
        try {
            this.object = object;
            this.id = UUID.randomUUID().toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 代理对象接口方法；
     * <p>
     * 代理AutoPerObject接口方法,实现数据库操作：
     * save,delete,query,clear,count,getQuery,getQueryPage...
     * </p>
     * <p>
     * 代理数据库对象接口属性操作方法
     * </p>
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (persObject == null) {
            persObject = (AutopersObject) proxy;
        }

        if (method.getDeclaringClass().equals(AutopersObject.class)) {//默认代理接口，非组合类,自动生成的接口代理


            if (method.getName().equals("equals")) {//
                if (args[0] == null)
                    return false;
                if (args[0] instanceof AutopersObject) {
                    if (((AutopersObject) args[0])._AutopersObjectId().equals(id)) {
                        return true;
                    }
                }
                return false;
            }
            if (method.getName().equals("_AutopersObjectId")) {//
                return this.id;
            }
            if (method.getName().equals("_GetFields")) {
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                return fields;
            }

        } else if (!method.getDeclaringClass().equals(object) && method.getName().equals("getClass")) {
            // getClass
            return object;
        } else  {

            // AutopersMapping，自动生成实体 接口定义的方法
            String field = getFielNameByMethod(method);
            if (method.getName().startsWith("set")) {//设置对象属性操作
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                AutopersObjectField objectField = fields.get(field);
                if (objectField == null)
                    objectField =new AutopersObjectFieldDefault(args[0], field, "");
                else
                    objectField.setValue(args[0]);
                fields.put(field, objectField);
                return null;
            } else if (method.getName().startsWith("get")) {//获取对象属性操作
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                AutopersObjectField objectField = fields.get(field);
                if (objectField == null)
                    objectField = new AutopersObjectFieldDefault(null, field, "");
                fields.put(field, objectField);

                if (AutopersReflectClass.isImplements(method.getReturnType(), IAutopersType.class)) {

                    if (objectField.getValue() != null) {
                        if (objectField.getValue().getClass().equals(String.class)) {
                            return method.getReturnType().getConstructor(String.class).newInstance(objectField.getValue());
                        } else
                            return objectField.getValue();
                    } else
                        return null;
                } else
                    return objectField.getValue();
            }



        }

        return null;
    }

    /**
     * 依据对象get、set方法获取对应对象之属性
     *
     * @param method
     * @return
     */
    private String getFielNameByMethod(Method method) {
        String methodName = method.getName().substring(3);
        if (methodName.length() == 0)
            return methodName.toLowerCase();
        methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        return methodName;

    }


}

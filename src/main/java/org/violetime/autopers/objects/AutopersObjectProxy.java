package org.violetime.autopers.objects;
import org.violetime.autopers.annotation.AutopersAnnotation;
import org.violetime.autopers.database.DataBaseFactory;
import org.violetime.autopers.database.DataBaseSource;
import org.violetime.autopers.function.AutopersFunction;
import org.violetime.autopers.function.AutopersFunctionFactory;
import org.violetime.autopers.mapping.AutopersMapping;
import org.violetime.autopers.mapping.AutopersMappingClass;
import org.violetime.autopers.mapping.AutopersMappingField;
import org.violetime.autopers.mapping.IAutopersMappingField;
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
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

/**
 * 数据库实体接口代理类
 *
 * @author Administrator
 */
public class AutopersObjectProxy implements InvocationHandler {
    private final static Logger logger = Logger.getLogger("AutopersObjectProxy");
    private Class<?> object;//代理对象接口
    private AutopersObject persObject;//代理对象
    private AutopersSession session;//数据库会话对象
    private AutopersQuery query;//查询对象
    private HashMap<String, AutopersObjectField> fields;//代理对象属性集合
    private AutopersObjectField lastGetField;
    private AutopersQueryPage queryPage;//查询分页对象
    private Map<String, AutopersMappingClass> mapping;//xml类映射对象定义
    private Map<String, Class<?>> mappingClass;//xml类映射对象类
    private boolean isCombine = false;//组合类对象专用
    private String id;
    private boolean hasResult = false;
    /**
     * 对象代理类构造函数
     *
     * @param object 代理接口-对象接口
     */
    public AutopersObjectProxy(Class<?> object) {
        try {
            this.object = object;
            this.id = UUID.randomUUID().toString();
            initMappingClass();
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
        //默认代理接口，非组合类,自动生成的接口代理
        //定义结果集
        if (method.getName().equals("_Result")) {
            if (lastGetField == null)
                return null;
            lastGetField.setResult(true);
            if(args.length==1){
                Object fieldValue = args[0];
                AutopersFunction function = AutopersFunctionFactory.newAutopersFunction(lastGetField);
                hasResult = true;
                return function;
            }
            for(Object arg:args){
                Object fieldValue = arg;
                AutopersFunction function = AutopersFunctionFactory.newAutopersFunction(lastGetField);
            }
            hasResult = true;
            return null;
        }
        //定义结果集
        if (method.getName().equals("_HasResult")) {
            return hasResult;
        }
        if (method.getDeclaringClass().equals(AutopersObject.class)) {

            // AutopersObject 接口定义的方法
            if (method.getName().equals("_Save")) {
                AutopersSession persSession = initAutopersSession();
                Object result = null;
                try {
                    if (persSession == null) {
                        throw new Exception(" AutopersSession  is  null when object.save invoke");
                    }
                    result = persSession.saveObject(this.persObject);
                    if (persSession != null) {
                        persSession.close();
                    }
                } catch (Exception e) {
                    if (persSession != null) {
                        persSession.close();
                    }
                    throw e;
                }
                return result;
            }
            if (method.getName().equals("_Delete")) {//数据库删除操作
                AutopersSession persSession = initAutopersSession();
                Object result = null;
                try {
                    if (persSession == null) {
                        throw new Exception(" AutopersSession  is  null when object.delete invoke");
                    }
                    if (method.getParameterCount() == 0) {
                        if(query!=null)
                            result = persSession.deleteQuery(query);
                        else
                            result = persSession.deleteObject(this.persObject);
                    } else if (method.getParameterCount() == 1) {
                        Parameter parameter= method.getParameters()[0];
                        if(parameter.getType().equals(AutopersQuery.class)){
                            result = persSession.deleteQuery((AutopersQuery) args[0]);
                        }else {
                            List<AutopersObject> list= (List<AutopersObject>) args[0];
                            result = persSession.deleteList(list,persObject);
                        }
                    }
                    if (persSession != null) {
                        persSession.close();
                    }
                } catch (Exception e) {
                    if (persSession != null) {
                        persSession.close();
                    }
                    throw e;
                }
                return result;
            }
            if (method.getName().equals("_Clear")) {//对象属性清空操作
                if (this.fields != null)
                    this.fields = null;
                if (this.query != null)
                    this.query = null;
                if (this.queryPage != null)
                    this.queryPage = null;
            }
            if (method.getName().equals("_Mapping")) {//获取mapping
                if (this.mapping == null || this.mapping.size() == 0)
                    return null;
                return this.mapping.values().stream().findFirst().get();
            }
            if (method.getName().equals("_Data")) {//数据库查询操作
                AutopersSession persSession = initAutopersSession();
                Object result = null;
                try {
                    if (persSession == null) {
                        throw new Exception(" AutopersSession  is  null when object.query invoke");
                    }
                    if (method.getParameterCount() == 0) {
                        String codeId = AutopersQueryTool.getCodeIdFromObjectStatic(4);
                        persSession.codeId(codeId);
                        if (query != null && query.getMethods() != null && query.getMethods().size() > 0) {
                            if (args == null) {
                                result = persSession.query(query);
                            } else {
                                result = persSession.query((AutopersQuery) args[0]);
                            }
                        } else {
                            result = persSession.queryObject(this.persObject);
                        }
                    } else if (method.getParameterCount() == 1) {
                        if (args[0].getClass().equals(String.class)) {
                            persSession.codeId((String) args[0]);
                            if (query != null && query.getMethods() != null && query.getMethods().size() > 0) {
                                result = persSession.query(query);
                            } else {
                                result = persSession.queryObject(this.persObject);
                            }
                        } else {
                            String codeId = AutopersQueryTool.getCodeIdFromObjectStatic(4);
                            persSession.codeId(codeId);
                            result = persSession.query((AutopersQuery) args[0]);
                        }
                    }
                    persSession.codeId(null);
                    if (persSession != null) {
                        persSession.close();
                    }
                } catch (Exception e) {
                    if (persSession != null) {
                        persSession.close();
                    }
                    throw e;
                }
                return result;
            }
            if (method.getName().equals("_Count")) {//数据库查询个数操作
                AutopersSession persSession = initAutopersSession();
                Object result = null;
                try {
                    if (persSession == null) {
                        throw new Exception(" AutopersSession  is  null when object.count invoke");
                    }
                    if (method.getParameterCount() == 0) {
                        if (query != null && query.getMethods() != null && query.getMethods().size() > 0) {
                            result = persSession.queryCount(query);
                        } else {
                            result = persSession.queryObjectCount(this.persObject);
                        }

                    } else if (method.getParameterCount() == 1) {
                        result = persSession.queryCount((AutopersQuery) args[0]);
                    }
                    if (persSession != null) {
                        persSession.close();
                    }
                } catch (Exception e) {
                    if (persSession != null) {
                        persSession.close();
                    }
                    throw e;
                }
                return result;
            }
            if (method.getName().equals("_GetMappingClass")) {
                return mappingClass;
            }
            if (method.getName().equals("_GetProxyClass")) {
                return object;
            }
            if (method.getName().equals("_IsCombine")) {
                return isCombine;
            }
            if (method.getName().equals("_GetMapping")) {
                return mapping;
            }
            if (method.getName().equals("_GetFields")) {
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                return fields;
            }
            if (method.getName().equals("_PutField")) {
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                AutopersObjectField  objectField= (AutopersObjectField) args[0];
                fields.put(objectField.getField(),objectField);
                return null;
            }

            if (method.getName().equals("_Query")) {//获取查询对象
                if (this.query == null) {
                    this.query = AutopersQueryFactory.newAutopersQueryObject();
                    this.query.setAutopersObject(persObject);
                }
                query.clearFields();
                return query;
            }
            if (method.getName().equals("_Page")) {//设置查询分页
                if (method.getParameterCount() == 0) {
                    return this.queryPage;
                } else if (method.getParameterCount() == 1) {
                    this.queryPage = (AutopersQueryPage) args[0];
                } else if (method.getParameterCount() == 2) {
                    AutopersQueryPageDefault pageDefault = new AutopersQueryPageDefault();
                    pageDefault.setNum((int) args[1]);
                    pageDefault.setPage((int) args[0]);
                    this.queryPage = pageDefault;
                }
            }
            if (method.getName().equals("equals")) {//获取查询分
                if (args[0] == null)
                    return false;
                if (args[0] instanceof AutopersObject) {
                    if (((AutopersObject) args[0])._AutopersObjectId().equals(id)) {
                        return true;
                    }
                }
                return false;
            }
            if (method.getName().equals("_AutopersObjectId")) {//获取查询分页
                return this.id;
            }
        } else if (!method.getDeclaringClass().equals(object) && method.getName().equals("getClass")) {
            // getClass
            return object;
        } else if (!isCombine) {
            Class mapCls = this.mappingClass.get(method.getDeclaringClass().getName());
            if (mapCls == null) {
                throw new Exception(" mapCls  is  null when Method invoke");
            }
            // AutopersMapping，自动生成实体 接口定义的方法
            String field = getFielNameByMethod(method);
            AutopersMappingClass mappingClass= this.mapping.values().stream().findFirst().get();
            IAutopersMappingField mappingField=mappingClass.getField(field);
            if (method.getName().startsWith("set")) {//设置对象属性操作
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                AutopersObjectField objectField = fields.get(field);
                if (objectField == null)
                    objectField = AutopersObjectsFactory.newInstanceField(args[0], field, mapCls.getName(),mappingField.getJavatype());
                else
                    objectField.setValue(args[0]);
                fields.put(field, objectField);
                return null;
            } else if (method.getName().startsWith("get")) {//获取对象属性操作
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                AutopersObjectField objectField = fields.get(field);
                if (objectField == null)
                    objectField = AutopersObjectsFactory.newInstanceField(null, field, mapCls.getName(),mappingField.getJavatype());
                fields.put(field, objectField);
                if (query != null)
                    query.addField(objectField);
                lastGetField = objectField;
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
        } else if (isCombine) {
            // 自定义组合实体 接口定义的方法
            Class mappClass = null;
            for (Object key : this.mappingClass.keySet()) {
                Class mappCls = this.mappingClass.get(key);
                Method mappMethod = null;
                try {
                    mappMethod = mappCls.getMethod(method.getName(), method.getParameterTypes());
                } catch (Exception e) {
                    //  e.printStackTrace();
                }
                if (mappMethod != null) {
                    mappClass = mappCls;
                }
            }
            if (mappClass == null) {
                throw new Exception(" mappClass  is  null when Method invoke");
            }
            String field = getFielNameByMethod(method);
            AutopersMappingClass  mc=mapping.get(mappClass.getName());

            if (method.getName().startsWith("set")) {
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                AutopersObjectField objectField = fields.get(field);
                if (objectField == null) {
                    objectField = AutopersObjectsFactory.newInstanceField(args[0], field, mappClass.getName(), mc.getField(field).getJavatype());
                    dealCombineDict(objectField, method);
                } else
                    objectField.setValue(args[0]);
                fields.put(field, objectField);
                return null;
            } else if (method.getName().startsWith("get")) {
                if (fields == null)
                    fields = new HashMap<String, AutopersObjectField>();
                AutopersObjectField objectField = fields.get(field);
                if (objectField == null) {
                    {
                        objectField = AutopersObjectsFactory.newInstanceField(null, field, mappClass.getName(), mc.getField(field).getJavatype());
                        dealCombineDict(objectField, method);
                    }
                    fields.put(field, objectField);
                }

                if (query != null)
                    query.addField(objectField);
                lastGetField=objectField;
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
    private void dealCombineDict(AutopersObjectField objectField, Method method) throws NoSuchMethodException {
        Annotation[] annotations = method.getAnnotationsByType(AutopersAnnotation.CombineField.class);
        if (annotations != null && annotations.length > 0) {
            AutopersAnnotation.CombineField combineDictionary = (AutopersAnnotation.CombineField) annotations[0];
            String annField = combineDictionary.field();
            objectField.setCombine(true);
            objectField.setCombineField(annField);
        }
    }
    public void setSession(AutopersSession session) {
        this.session = session;
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
    /**
     * 初始化 对象映射xml定义类
     *
     * @throws Exception
     */
    private void initMappingClass() throws Exception {
        if (this.mapping == null) {
            this.mapping = new LinkedHashMap<>();
            this.mappingClass = new LinkedHashMap<>();
            Annotation[] annotations = object.getAnnotationsByType(AutopersAnnotation.Combine.class);
            if (annotations != null && annotations.length > 0) {
                isCombine = true;
                for (Class parent : object.getInterfaces()) {
                    boolean flag = false;
                    for (Class sup : parent.getInterfaces()) {
                        if (sup.equals(AutopersObject.class)) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        continue;
                    }
                    Class<?> xmlClass = AutopersObjectsUnit.getObjectXmlClass(parent);
                    if (xmlClass == null)
                        throw new Exception(" xmlClass  is  null when AutopersObjectProxy invoke");
                    AutopersMappingClass mapp = AutopersMapping.getMappingClassXml(xmlClass);
                    this.mapping.put(parent.getName(), mapp);
                    this.mappingClass.put(parent.getName(), Class.forName(mapp.getClassPath()));
                }
                /**
                 * 加载组合类接口中的字典类组合方法
                 */
                for (Method method : object.getMethods()) {
                    Annotation[] methodAnnotations = method.getAnnotationsByType(AutopersAnnotation.CombineField.class);
                    if (methodAnnotations != null && methodAnnotations.length > 0) {
                        Class mappClass = null;
                        for (Object key : this.mappingClass.keySet()) {
                            Class mappCls = this.mappingClass.get(key);
                            Method mappMethod = null;
                            try {
                                mappMethod = mappCls.getMethod(method.getName(), method.getParameterTypes());
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }
                            if (mappMethod != null) {
                                mappClass = mappCls;
                            }
                        }
                        if (mappClass == null) {
                            throw new Exception(" mappClass  is  null when Method invoke");
                        }
                        if (fields == null)
                            fields = new HashMap<String, AutopersObjectField>();
                        String field = getFielNameByMethod(method);
                        AutopersObjectField objectField = fields.get(field);
                        if (objectField == null) {
                            {
                                objectField = AutopersObjectsFactory.newInstanceField(null, field, mappClass.getName());
                                dealCombineDict(objectField, method);
                            }
                            fields.put(field, objectField);
                        }
                    }
                }
            } else {
                Class<?> xmlClass = AutopersObjectsUnit.getObjectXmlClass(object);
                if (xmlClass == null)
                    throw new Exception(" xmlClass  is  null when AutopersObjectProxy invoke");
                AutopersMappingClass mapp = AutopersMapping.getMappingClassXml(xmlClass);
                this.mapping.put(object.getName(), mapp);
                this.mappingClass.put(object.getName(), Class.forName(mapp.getClassPath()));
            }
        }
    }
    /**
     * 如果按照实体类请求不到资源，就使用默认数据连接配置资源
     *
     * @return
     */
    private AutopersSession initAutopersSession() {
        if (this.session != null)
            return this.session;
        else {
            for (Object key : mapping.keySet()) {
                AutopersMappingClass mappingClass = mapping.get(key);
                DataBaseSource baseSource = DataBaseFactory.getDataBaseSource(mappingClass);
                if (baseSource == null)
                    baseSource = DataBaseFactory.getDataBaseSource();
                return AutopersSessionFactory.openSession(baseSource);
            }
        }
        return null;
    }
}

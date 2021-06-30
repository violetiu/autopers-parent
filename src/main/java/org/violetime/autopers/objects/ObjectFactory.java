package org.violetime.autopers.objects;
import com.alibaba.fastjson.JSONObject;
import org.violetime.autopers.session.AutopersSession;
import org.violetime.autopers.session.AutopersSessionFactory;
import org.violetime.autopers.session.tool.AutopersQueryTool;
import org.violetime.autopers.units.AutopersCodeName;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *<p>数据库表接口代理类工厂
 * <ul>
 *     <li>用于创建数据库表接口代理类:_newInstanceObject,newInstanceObject</li>
 *     <li>对代理类List集合进行分组操作:group</li>
 *     <li>使用JSON对象和Request请求数据初始化代理类:loadJSONObject,loadRequest</li>
 *     <li>查询数据库操作:_Query,_Object</li>
 * </ul>
 * @param <T> T extends AutopersObject
 */
public class ObjectFactory<T extends AutopersObject> {
    private Class autopersObjectClass;

    public void print(){
        System.out.println( this.autopersObjectClass.getName());
    }

    public ObjectFactory(Class autopersObjectClass) {

        for (int index = 0; index < autopersObjectClass.getInterfaces().length; index++) {
            Class inter = autopersObjectClass.getInterfaces()[index];
            if (inter.equals(AutopersObject.class)) {
                this.autopersObjectClass = autopersObjectClass;
                return;
            }
            Class<?>[] inters = inter.getInterfaces();
            if (inters != null && inters.length > 0) {
                for (Class<?> aClass : inters) {
                    if (aClass.equals(AutopersObject.class)) {
                        this.autopersObjectClass = autopersObjectClass;
                        return;
                    }
                }
            }
        }
        new Exception("使用了非AutopersObject接口").printStackTrace();
    }
    public Map<String, List<T>> group(List<T> list, String attr, Function<String, String> function) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Method method = null;
        try {
            method = autopersObjectClass.getMethod("get" + AutopersCodeName.attributeGetSetName(attr));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (method == null)
            return null;
        Map<String, List<T>> listMap = new LinkedHashMap<>();
        Method finalMethod = method;
        list.forEach(t -> {
            String key = null;
            try {
                key = function.apply(finalMethod.invoke(t).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (key != null) {
                List<T> ts = listMap.get(key);
                if (ts == null) {
                    ts = new ArrayList<>();
                    listMap.put(key, ts);
                }
                ts.add(t);
            }
        });
        return listMap;
    }
    /**
     * 按对象属性进行分组
     * @param list
     * @param attr
     * @return
     */
    public Map<String, List<T>> group(List<T> list, String attr) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Method method = null;
        try {
            method = autopersObjectClass.getMethod("get" + AutopersCodeName.attributeGetSetCaseName(attr));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (method == null)
            return null;
        Map<String, List<T>> listMap = new LinkedHashMap<>();
        Method finalMethod = method;
        list.forEach(t -> {
            String key = null;
            try {
                Object rs= finalMethod.invoke(t);
                if(rs!=null)
                key =rs.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (key != null) {
                List<T> ts = listMap.get(key);
                if (ts == null) {
                    ts = new ArrayList<>();
                    listMap.put(key, ts);
                }
                ts.add(t);
            }
        });
        return listMap;
    }
    /**
     * 按对象属性进行分组
     *
     * @param list
     * @param attrAction
     * @return
     */
    public Map<String, List<T>> group(List<T> list, Function<T, Object> attrAction) {
        if (list == null || list.size() == 0) {

            return null;
        }

        T obj = newInstanceObject();
        attrAction.apply(obj);
        Map<String, AutopersObjectField> hashMap = obj._GetFields();
        if (hashMap == null||hashMap.size()==0) {
            try {
                if(hashMap==null){
                    System.out.println("_GetFields() is null");
                }else{
                    System.out.println("_GetFields() size is "+hashMap.size());
                }
                throw new Exception("分组对象属性设置错误");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
        List<Method> methods=new ArrayList<>();
        for (String attr : hashMap.keySet()) {
            Method method = null;
            try {
                method = autopersObjectClass.getMethod("get" + AutopersCodeName.attributeGetSetCaseName(attr));
                methods.add(method);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (methods == null)
            return null;
        Map<String, List<T>> listMap = new LinkedHashMap<>();
        list.forEach(t -> {
            String key = "";
            for (Method method : methods) {
                try {
                    Object rs=method.invoke(t);
                    if(rs!=null)
                        key+=rs.toString()+"|";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(key.endsWith("|"))
                key=key.substring(0,key.length()-1);
            if (key != null&&key.length()>0) {
                List<T> ts = listMap.get(key);
                if (ts == null) {
                    ts = new ArrayList<>();
                    listMap.put(key, ts);
                }
                ts.add(t);
            }
        });
        return listMap;
    }
    /**
     * 按对象属性进行分组
     * @param list
     * @param attrAction
     * @return
     */
    public Map<String, List<T>> group(List<T> list, Function<T, Object> attrAction, Function<String, String> function) {
        if (list == null || list.size() == 0) {
          
            return null;
        }
        T obj = newInstanceObject();
        attrAction.apply(obj);
        Map<String, AutopersObjectField> hashMap = obj._GetFields();
        if (hashMap == null || hashMap.size() != 1) {
            try {
                throw new Exception("分组对象属性设置错误");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
        String attr = hashMap.keySet().stream().findFirst().get();
        Method method = null;
        try {
            method = autopersObjectClass.getMethod("get" + AutopersCodeName.attributeGetSetCaseName(attr));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (method == null)
            return null;
        Map<String, List<T>> listMap = new LinkedHashMap<>();
        Method finalMethod = method;
        list.forEach(t -> {
            String key = null;
            try {
                key = function.apply(finalMethod.invoke(t).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (key != null) {
                List<T> ts = listMap.get(key);
                if (ts == null) {
                    ts = new ArrayList<>();
                    listMap.put(key, ts);
                }
                ts.add(t);
            }
        });
        return listMap;
    }

	/**
	 * 使用JSONObject加载对象属性值
	 * @param object
	 * @param jsonObject
	 */
    public void loadJSONObject(T object, JSONObject jsonObject) {
        if (jsonObject == null || object == null) {
            return;
        }
        jsonObject.forEach((key, value) -> {
            try {
                String methodName = "set" + key.toString().substring(0, 1).toUpperCase() + key.toString().substring(1);
                Method method = null;
                for (Method m : autopersObjectClass.getMethods()) {
                    if (m.getName().equals(methodName)) {
                        method = m;
                        break;
                    }
                }
                if (method != null) {
                    if (method.getParameters()[0].getType().equals(Double.class)) {
                        method.invoke(object, Double.parseDouble(value.toString()));
                    } else if (method.getParameters()[0].getType().equals(Integer.class)) {
                        method.invoke(object, Integer.parseInt(value.toString()));
                    } else if (method.getParameters()[0].getType().equals(Boolean.class)) {
                        method.invoke(object, Boolean.parseBoolean(value.toString()));
                    } else {
                        method.invoke(object, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * 使用HttpServletRequest加载对象属性值
     *
     * @param object
     * @param request
     */
    public void loadRequest(T object, HttpServletRequest request) {
        for (Object key : request.getParameterMap().keySet()) {
            Object value = request.getParameter(key.toString());
            if (value != null) {
                try {
                    String methodName = "set" + key.toString().substring(0, 1).toUpperCase() + key.toString().substring(1);
                    Method method = null;
                    for (Method m : autopersObjectClass.getMethods()) {
                        if (m.getName().equals(methodName)) {
                            method = m;
                            break;
                        }
                    }
                    if (method != null) {
                        if (method.getParameters()[0].getType().equals(Double.class)) {
                            method.invoke(object, Double.parseDouble(value.toString()));
                        } else if (method.getParameters()[0].getType().equals(Integer.class)) {
                            method.invoke(object, Integer.parseInt(value.toString()));
                        } else if (method.getParameters()[0].getType().equals(Boolean.class)) {
                            method.invoke(object, Boolean.parseBoolean(value.toString()));
                        } else {
                            method.invoke(object, value);
                        }
                    }
                } catch (Exception e) {
                    //	e.printStackTrace();
                }
            }
        }
    }
	/**
	 * 使用JSONObject加载对象属性值
	 * @param jsonObject
	 */
	public T loadJSONObject( JSONObject jsonObject) {
		if (jsonObject == null ) {
			return null;
		}
		T object=newInstanceObject();
		jsonObject.forEach((key, value) -> {
			try {
				String methodName = "set" + key.toString().substring(0, 1).toUpperCase() + key.toString().substring(1);
				Method method = null;
				for (Method m : autopersObjectClass.getMethods()) {
					if (m.getName().equals(methodName)) {
						method = m;
						break;
					}
				}
				if (method != null) {
					if (method.getParameters()[0].getType().equals(Double.class)) {
						method.invoke(object, Double.parseDouble(value.toString()));
					} else if (method.getParameters()[0].getType().equals(Integer.class)) {
						method.invoke(object, Integer.parseInt(value.toString()));
					} else if (method.getParameters()[0].getType().equals(Boolean.class)) {
						method.invoke(object, Boolean.parseBoolean(value.toString()));
					} else {
						method.invoke(object, value);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return object;
	}
	/**
	 * 使用HttpServletRequest加载对象属性值
	 * @param request
	 */
	public T loadRequest( HttpServletRequest request) {
		T object=newInstanceObject();
		for (Object key : request.getParameterMap().keySet()) {
			Object value = request.getParameter(key.toString());
			if (value != null) {
				try {
					String methodName = "set" + key.toString().substring(0, 1).toUpperCase() + key.toString().substring(1);
					Method method = null;
					for (Method m : autopersObjectClass.getMethods()) {
						if (m.getName().equals(methodName)) {
							method = m;
							break;
						}
					}
					if (method != null) {
						if (method.getParameters()[0].getType().equals(Double.class)) {
							method.invoke(object, Double.parseDouble(value.toString()));
						} else if (method.getParameters()[0].getType().equals(Integer.class)) {
							method.invoke(object, Integer.parseInt(value.toString()));
						} else if (method.getParameters()[0].getType().equals(Boolean.class)) {
							method.invoke(object, Boolean.parseBoolean(value.toString()));
						} else {
							method.invoke(object, value);
						}
					}
				} catch (Exception e) {
					//	e.printStackTrace();
				}
			}
		}
		return  object;
	}
    /**
     * 实例化一个数据库表对象
     *
     * @return
     */
    public T newInstanceObject() {
        Class[] interfaces = new Class[autopersObjectClass.getInterfaces().length + 1];
        for (int index = 0; index < autopersObjectClass.getInterfaces().length; index++) {
            interfaces[index] = autopersObjectClass.getInterfaces()[index];
        }
        interfaces[autopersObjectClass.getInterfaces().length] = autopersObjectClass;
        AutopersObjectProxy objectProxy = new AutopersObjectProxy(autopersObjectClass);
        T persObject = (T) Proxy.newProxyInstance(objectProxy.getClass().getClassLoader(), interfaces, objectProxy);
        return persObject;
    }
    /**
     * 实例化一个数据库表对象
     *
     * @param session
     * @return
     */
    public T newInstanceObject(AutopersSession session) {
        Class[] interfaces = new Class[autopersObjectClass.getInterfaces().length + 1];

        for (int index = 0; index < autopersObjectClass.getInterfaces().length; index++) {
            interfaces[index] = autopersObjectClass.getInterfaces()[index];
        }
        interfaces[autopersObjectClass.getInterfaces().length] = autopersObjectClass;
        AutopersObjectProxy objectProxy = new AutopersObjectProxy(autopersObjectClass);
        objectProxy.setSession(session);
        T persObject = (T) Proxy.newProxyInstance(objectProxy.getClass().getClassLoader(), interfaces, objectProxy);
        return persObject;
    }
    /***查询数据 */
    public List<T> _Query(Function<T, Object> queryAction) {
        T obj = newInstanceObject();
        queryAction.apply(obj);
        List<T> result = new ArrayList<>();
        try {
            List<AutopersObject> persObjectList = ((AutopersObject) obj)._Data(AutopersQueryTool.getCodeIdFromObjectStatic(3));
            if (persObjectList != null && persObjectList.size() > 0) {
                persObjectList.forEach(o -> result.add((T) o));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    /***查询数据 */
    public List<T> _Query(T object) {
        List<T> result = new ArrayList<>();
        try {
            List<AutopersObject> persObjectList = ((AutopersObject) object)._Data(AutopersQueryTool.getCodeIdFromObjectStatic(3));
            if (persObjectList != null && persObjectList.size() > 0) {
                persObjectList.forEach(o -> result.add((T) o));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    /***查询单个数据 */
    public T _Object(T object) {
        try {
            List<AutopersObject> persObjectList = ((AutopersObject) object)._Data(AutopersQueryTool.getCodeIdFromObjectStatic(3));
            if (persObjectList != null && persObjectList.size() > 0) {
                return (T) (persObjectList.get(0));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * <p>按照集合内对象的特定属性值，删除集合</p>
     *
     * @param list
     * @param action obj ->obj.getField0()+obj.getField1()，按照field0,field1属性的值去删除该集合
     * @return
     * @throws Exception
     */
    public Long deleteList(List<? extends AutopersObject> list, Function<T,Object> action) throws Exception{
       //优化删除结合过程，使用对集合内对象的属性值的分析，缩减删除语句的数据量
        //具体操作：使用统计对象各属性在集合内出现的次数，依次数少的属性为删除语句，次数多的属性为in语句。
        //操作原理：每条删除语句，确定次数少的数据，次数最多的为可变写在in语句中。

        AutopersSession session = AutopersSessionFactory.openSession();
        try{
            T obj=newInstanceObject();
            action.apply(obj);
            return session.deleteList(list,obj);
        }catch (Exception e){
            throw  e;
        }finally {
            session.close();
        }

    }
    /**
     * 插入操作
     * @param autoPersObjects
     * @return
     * @throws SQLException
     */
    public Long insertList(List<? extends AutopersObject> autoPersObjects) throws SQLException{
        AutopersSession session = AutopersSessionFactory.openSession();
        try{
            return session.insertList(autoPersObjects);
        }catch (Exception e){
            throw  e;
        }finally {
            session.close();
        }

    }

    /**
     * 对集合进行分页
     * @param list
     * @param page
     * @param num
     * @return
     */
    public List<T> pagination(List<T> list,int page,int num) {
       int start=page*num;
       int end=(page+1)*num;
       if(start>=list.size()){
           return null;
       }
       if(end>list.size())
           end=list.size();

       return list.subList(start,end);
    }
}

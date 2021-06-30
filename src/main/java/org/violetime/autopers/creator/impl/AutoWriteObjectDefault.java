package org.violetime.autopers.creator.impl;

import org.violetime.autopers.database.DataBaseColumn;
import org.violetime.autopers.database.DataBaseTable;
import org.violetime.autopers.units.AutopersCodeName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自动写入数据库实体对象类文件
 *
 * @author taoyo
 */
public class AutoWriteObjectDefault {
    private static HashMap<String, String> dataTypeMap;
    public static HashMap<String, String> getDataTypeMap() {
        return dataTypeMap;
    }
    public static void setDataTypeMap(HashMap<String, String> dataTypeMap) {
        AutoWriteObjectDefault.dataTypeMap = dataTypeMap;
    }
    public static void autoWrite(List<DataBaseColumn> baseColumns, DataBaseTable baseTable, String path, String classPackage, String baseSource)
            throws Exception {
        File mappingPath = new File(path);
        if (!mappingPath.exists()) {
            mappingPath.mkdir();
        }

        baseColumns.sort((o1, o2) -> (
                o1.getColnumName().compareTo(o2.getColnumName())
                ));

        String className;
        String classPath;
        if (path.contains("\\"))
            path = path.replace("\\", "/");
        if (path.contains("%20"))
            path = path.replace("%20", " ");
        className = AutopersCodeName.className(baseTable.getTableName().toLowerCase(), baseSource);
        if (path.endsWith("/")) {
            classPath = path + "" + className + ".java";
        } else {
            classPath = path + "/" + className + ".java";
        }
        // begin auto write fileeWriter
        FileWriter fileWriter = new FileWriter(new File(classPath));
        fileWriter.write("package " + classPackage + ";");
        fileWriter.write("\n");
        // java for import
        fileWriter.write("import org.violetime.autopers.objects.AutopersObject;");
        fileWriter.write("\n");
        fileWriter.write("import org.violetime.autopers.objects.AutopersObjectsFactory;");
        fileWriter.write("\n");
        fileWriter.write("import java.sql.SQLException;");
        fileWriter.write("\n");
        fileWriter.write("import java.util.ArrayList;");
        fileWriter.write("\n");
        fileWriter.write("import java.util.List;");
        fileWriter.write("\n");
        fileWriter.write("import java.util.function.Function;");
        fileWriter.write("\n");
        fileWriter.write("import org.violetime.autopers.session.tool.AutopersQueryTool;");
        fileWriter.write("\n");
        fileWriter.write("/**");
        fileWriter.write("<p>" + baseTable.getComment() + "<p><p>数据库表修改时间：" + baseTable.getUpdateTime() + "<p><p>实体类写入时间："
                + new Date().toString()+"<p>");
        fileWriter.write("@author autopers");
        fileWriter.write("\n<ul>");
        for (DataBaseColumn baseColumn : baseColumns) {
            fileWriter.write("<li>"+baseColumn.getColnumName() + " " + baseColumn.getComment() + "</li>");
        }
        fileWriter.write("</ul>\n");
        fileWriter.write(" */");
        fileWriter.write("\n");
        // write class
        fileWriter.write("public interface " + className + " extends AutopersObject  {");
        fileWriter.write("\n");

        fileWriter.write("\t/**");
        fileWriter.write("*创建一个实例");
        fileWriter.write(" */\n");
        fileWriter.write("\tpublic static " + className + " _Instance() {");
        fileWriter.write("\n");
        fileWriter.write("\t\t" + className + " object=(" + className + ")AutopersObjectsFactory.newInstanceObject(" + className
                + ".class);\n");
        fileWriter.write("\t\treturn object;\n");
        fileWriter.write("\t}\n");

        StringBuffer setCode=new StringBuffer();
        StringBuffer argsCode=new StringBuffer();
        StringBuffer getSetCode=new StringBuffer();
        //设置参数
        for (DataBaseColumn baseColumn : baseColumns) {
            String columnName = baseColumn.getColnumName().toUpperCase();
            String javaClass = dataTypeMap.get(baseColumn.getJdbcType().toUpperCase());
            if (javaClass.equals("java.lang.String") && baseColumn.getComment() != null) {
                Map<String, String> commentMap = dealColumnComment(baseColumn.getComment());
                if (commentMap == null || commentMap.size() < 2) {

                } else {
                    String typeName=AutopersCodeName.className(columnName);
                    if(typeName.endsWith("y")||typeName.endsWith("f")){
                        typeName=typeName.substring(0,typeName.length()-2)+"ies";
                    }else if(typeName.endsWith("fe")){
                        typeName=typeName.substring(0,typeName.length()-3)+"ies";
                    }else if(typeName.endsWith("o")||typeName.endsWith("s")||typeName.endsWith("x")||typeName.endsWith("ch")||typeName.endsWith("sh")){
                        typeName+="es";
                    }else{
                        typeName+="s";
                    }
                    //创建实体类属性的 静态枚举类
                    javaClass = classPackage + "." + typeName;
                    createFieldTypes(classPackage, javaClass, path, baseColumn.getComment(), commentMap);
                }
            }
            String getSetName = AutopersCodeName.attributeGetSetName(columnName);
            String  fieldName= AutopersCodeName.attributeName(columnName);
            setCode.append("\t\tautopersObject.set"+getSetName+"("+fieldName+");\n");
            argsCode.append(",").append(javaClass).append(" ").append(fieldName);

            if (baseColumn.getComment() != null && baseColumn.getComment().length() > 0) {
                getSetCode.append("\t/**");
                getSetCode.append("\n");
                getSetCode.append("\t获取" + baseColumn.getComment());
                getSetCode.append("\n");
                getSetCode.append("\t*/");
                getSetCode.append("\n");
            }
            getSetCode.append("\tpublic " + javaClass + " get" + getSetName + "();");
            getSetCode.append("\n");
            if (baseColumn.getComment() != null && baseColumn.getComment().length() > 0) {
                getSetCode.append("\n");
                getSetCode.append("\t/**");
                getSetCode.append("\n");
                getSetCode.append("\t设置" + baseColumn.getComment());
                getSetCode.append("\n");
                getSetCode.append("\t*/");
                getSetCode.append("\n");
            }

            getSetCode.append("\tpublic void set" + getSetName + "(" + javaClass + "  "
                    + fieldName+ ");");
            getSetCode.append("\n");
        }

        fileWriter.write("\t/**");
        fileWriter.write("*创建一个实例，携带参数");
        fileWriter.write(" */\n");
        fileWriter.write("\tpublic static " + className + " _Instance("+argsCode.substring(1)+") {");
        fileWriter.write("\n");
        fileWriter.write("\t\t" + className + " autopersObject=(" + className + ")AutopersObjectsFactory.newInstanceObject(" + className
                + ".class);\n");
        fileWriter.write(setCode.toString());
        fileWriter.write("\t\treturn autopersObject;\n");
        fileWriter.write("\t}\n");

        fileWriter.write("\t/**");
        fileWriter.write("*创建一个简单实例，不支持数据库操作,可以节省内存资源");
        fileWriter.write(" */\n");
        fileWriter.write("\tpublic static " + className + " _InstanceSimple() {");
        fileWriter.write("\n");
        fileWriter.write("\t\t" + className + " object=(" + className + ")AutopersObjectsFactory.newInstanceSimpleObject(" + className
                + ".class);\n");
        fileWriter.write("\t\treturn object;\n");
        fileWriter.write("\t}\n");
        fileWriter.write("\t/**");
        fileWriter.write("*查询数据");
        fileWriter.write(" */\n");
        fileWriter.write("\tpublic static List<" + className + "> _Data(" + className + " object) {\n");
        fileWriter.write("\t\tList<" + className + "> result=new ArrayList<>();\n");
        fileWriter.write("\t\ttry {\n");
        fileWriter.write("\t\t\tList<AutopersObject> persObjectList=object._Data(AutopersQueryTool.getCodeIdFromObjectStatic(3));\n");
        fileWriter.write("\t\t\tif(persObjectList!=null&&persObjectList.size()>0){\n");
        fileWriter.write("\t\t\t\tpersObjectList.forEach(o->result.add((" + className + ")o));\n");
        fileWriter.write("\t\t\t}else{\n");
        fileWriter.write("\t\t\treturn null;\n");
        fileWriter.write("\t\t\t}\n");
        fileWriter.write("\t\t} catch (SQLException e) {e.printStackTrace();}\n");
        fileWriter.write("\t\treturn result;\n");
        fileWriter.write("\t}\n");
        fileWriter.write("\t/**");
        fileWriter.write("*查询单个数据");
        fileWriter.write(" */\n");
        fileWriter.write("\tpublic static " + className + " _Object(" + className + " object) {\n");
        fileWriter.write("\t\ttry {\n");
        fileWriter.write("\t\t\tList<AutopersObject> persObjectList=object._Data(AutopersQueryTool.getCodeIdFromObjectStatic(3));\n");
        fileWriter.write("\t\t\tif(persObjectList!=null&&persObjectList.size()>0){\n");
        fileWriter.write("\t\t\t\treturn (" + className + ")(persObjectList.get(0));\n");
        fileWriter.write("\t\t\t}else{\n");
        fileWriter.write("\t\t\treturn null;\n");
        fileWriter.write("\t\t\t}\n");
        fileWriter.write("\t\t} catch (SQLException e) {e.printStackTrace();}\n");
        fileWriter.write("\t\treturn null;\n");
        fileWriter.write("\t}\n");
        fileWriter.write("\t/**");
        fileWriter.write("*删除多条数据");
        fileWriter.write(" */\n");
        fileWriter.write("\tpublic static Long _DeleteList(List<"+className+"> list, Function<"+className+",Object[]> delete) throws SQLException{\n");
        fileWriter.write("\t\t"+className+" object=_Instance();\n");
        fileWriter.write("\t\t\tdelete.apply(object);\n");
        fileWriter.write("\t\t\treturn object._Delete(list);\n");
        fileWriter.write("\t}\n");
        //Get Set
        fileWriter.write(getSetCode.toString());

        fileWriter.write("\n");
        // end class
        fileWriter.write("}");
        fileWriter.flush();
        fileWriter.close();
    }
    private static void createFieldTypes(String classPackage, String javaClass, String path, String comment, Map<String, String> commentMap) throws IOException {
        String className;
        String classPath;
        if (path.contains("\\"))
            path = path.replace("\\", "/");
        if (path.contains("%20"))
            path = path.replace("%20", " ");
        className = javaClass.replace(classPackage, "").substring(1);
        if (path.endsWith("/")) {
            classPath = path + "" + className + ".java";
        } else {
            classPath = path + "/" + className + ".java";
        }
        // begin auto write fileeWriter
        FileWriter fileWriter = new FileWriter(new File(classPath));
        fileWriter.write("package " + classPackage + ";");
        fileWriter.write("\n");
        fileWriter.write("import org.violetime.autopers.objects.IAutopersType;");
        fileWriter.write("\n");
        // java for import
        //
        fileWriter.write("/**");
        fileWriter.write("实体对象数属性选择类，自动写入时间："
                + new Date().toString());
        fileWriter.write("\n");
        fileWriter.write("*" + comment);
        fileWriter.write("\n");
        fileWriter.write("*@author autopers");
        fileWriter.write(" */");
        fileWriter.write("\n");
        // write class
        fileWriter.write("public class " + className + " implements IAutopersType  {");
        fileWriter.write("\n");
        fileWriter.write("\tprivate String type=\"\";");
        fileWriter.write("\n");
        fileWriter.write("\tpublic " + className + "(String type) {");
        fileWriter.write("\n");
        fileWriter.write("\t\tthis.type = type;");
        fileWriter.write("\n");
        fileWriter.write("\t}");
        fileWriter.write("\n");
        fileWriter.write("\t@Override");
        fileWriter.write("\n");
        fileWriter.write("\tpublic boolean equals(Object obj) {");
        fileWriter.write("\n");
        fileWriter.write("\t\treturn type.equals(obj.toString());");
        fileWriter.write("\n");
        fileWriter.write("\t}");
        fileWriter.write("\n");
        fileWriter.write("\t@Override");
        fileWriter.write("\n");
        fileWriter.write("\tpublic String toString() {");
        fileWriter.write("\n");
        fileWriter.write("\t\treturn type;");
        fileWriter.write("\n");
        fileWriter.write(" \t}");
        fileWriter.write("\n");
        fileWriter.write("\tpublic String getType() {");
        fileWriter.write("\n");
        fileWriter.write("\t\treturn type;");
        fileWriter.write("\n");
        fileWriter.write("\t}");
        fileWriter.write("\n");
        fileWriter.write("\tpublic void setType(String type) {");
        fileWriter.write("\n");
        fileWriter.write("\t\tthis.type = type;");
        fileWriter.write("\n");
        fileWriter.write("\t}");
        fileWriter.write("\n");
        for (Object key : commentMap.keySet()) {
            String info = commentMap.get(key);
            fileWriter.write("\tpublic static " + className + " " + AutopersCodeName.className(key.toString()) + "=new " + className + "(\"" + key.toString() + "\");");
            fileWriter.write("\n");
        }
        fileWriter.write("\n");
        fileWriter.write("\tpublic  String display(){");
        fileWriter.write("\n");
        fileWriter.write("\t\tswitch (type){");
        fileWriter.write("\n");
        for (Object key : commentMap.keySet()) {
            String info = commentMap.get(key);
            fileWriter.write("\t\t\tcase \"" + key + "\":return \"" + info + "\";");
            fileWriter.write("\n");
        }
        fileWriter.write("\t\t}");
        fileWriter.write("\n");
        fileWriter.write("\t\t\treturn  null;");
        fileWriter.write("\t}");
        fileWriter.write("}");
        fileWriter.flush();
        fileWriter.close();
    }
    public static Map<String, String> dealColumnComment(String comment) {

        Map<String, String> stringMap = null;
        String line = comment;//"时间维度,n2ode:阿萨德,kdsf:搜索";
        String pattern = "([0-z]+:\\W+)";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        while (m.find()) {
            String key = m.group(1).split(":")[0];
            String value = m.group(1).split(":")[1];
            if (value.trim().endsWith(",")) {
                value = value.trim().substring(0, value.trim().length() - 1);
            }
            if (stringMap == null)
                stringMap = new HashMap<>();
            stringMap.put(key, value);
        }
        return stringMap;
    }
}

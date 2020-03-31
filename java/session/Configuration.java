package session;

import annotation.*;
import binding.MapperRegistry;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

public class Configuration {
    //配置  类.方法名--SQL语句
    private final ResourceBundle statementMap = ResourceBundle.getBundle("sql");
    //配置  类.方法名--返回类型
    private final ResourceBundle returnTypeMap = ResourceBundle.getBundle("returnType");
    //配置  mybatis常用配置参数，数据库参数等
    private final ResourceBundle propertyMap = ResourceBundle.getBundle("mybatis");
    //用于缓存 类.方法名--返回类型
    private final Map<String, Class<?>> methodReturnTypeCache = new HashMap<>();
    //用于缓存 类.方法名--SQL
    private final Map<String, String> methodSqlCache = new HashMap<>();
    //用于缓存 类.方法名--SQL命令类型 （增删改查）
    private final Map<String, SqlCommandType> sqlCommandTypeCache = new HashMap<>();
    //用于缓存 mapper接口--MapperProxyFactory
    private final MapperRegistry mapperRegistry = new MapperRegistry();
    //用于缓存 .class文件
    private final List<String> classPaths = new LinkedList<>();
    //用于缓存mapper接口，用在解析注解时
    private final List<Class<?>> mapperList = new LinkedList<>();

    
    public Configuration() {
        init();
    }

    private void init() {
        /* 解析 类.方法名--SQL之间的配置，如果有注解，会被注解覆盖 */
        for (String key : statementMap.keySet()){
            methodSqlCache.put(key, statementMap.getString(key));
        }

        /* 解析 类.方法名--返回类型 之间的配置，如果有注解，会被注解覆盖 */
        for (String key : returnTypeMap.keySet()){
            methodReturnTypeCache.put(key, dealReturnType(returnTypeMap.getString(key)));
        }

        /* 解析mapper类上的注解 */
        scanForMappers(this.propertyMap.getString("mapperPath"));

        /* 解析mapper类的方法上的注解 */
        try {
            for (Class<?> clazz : mapperList) {
                parseMethodAnnotations(clazz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(methodReturnTypeCache);
        System.out.println(methodSqlCache);
        System.out.println("----------");
    }



    public Map<String, Class<?>> getMethodReturnTypeCache() {
        return methodReturnTypeCache;
    }

    public Map<String, SqlCommandType> getSqlCommandTypeCache() {
        return sqlCommandTypeCache;
    }

    public Map<String, String> getMethodSqlCache() {
        return methodSqlCache;
    }

    public ResourceBundle getReturnTypeMap() {
        return returnTypeMap;
    }

    public ResourceBundle getPropertyMap() {
        return propertyMap;
    }

    public <T> T getMapper(Class<T> clazz, SqlSession session) {
        return mapperRegistry.getMapper(clazz, session);
    }

    private void scanForMappers(String path) {
        String projectPath = Configuration.class.getResource("/").getPath();
        String fullPath = null;
        String mapperName = null;
        Class<?> clazz = null;

        if (null == path || path.isEmpty()){
            //从工程目录级别开始，挨个包扫
            fullPath = projectPath;
        }else {
            fullPath = projectPath + path.replace(".", "/");
        }
        scanFiles(new File(fullPath));

        //解析mapper类
        for (String filePath : classPaths){
            mapperName = filePath.replace("\\", "/").
                    replace(projectPath.replaceFirst("/", ""), "").
                    replace(".class", "").replace("/", ".");

            try {
                clazz = Class.forName(mapperName);
                for (Annotation annotation : clazz.getDeclaredAnnotations()){
                    if (annotation.annotationType().equals(Mapper.class)){
                        mapperList.add(clazz);
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void scanFiles(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f: files){
                if (f.isDirectory()){
                    scanFiles(f);
                }else {
                    if (f.getName().endsWith(".class")){
                        classPaths.add(f.getPath());
                    }
                }
            }
        }
    }

    private void parseMethodAnnotations(Class<?> clazz) throws SQLException {
        Method[] methods = clazz.getDeclaredMethods();
        String methodId = null;

        for (Method method : methods){
            methodId = method.getDeclaringClass().getName() + "." + method.getName();
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations){
                /* 增删改查各种类型 */
                if (annotation.annotationType().equals(Select.class)){
                    methodSqlCache.put(methodId, ((Select)annotation).value());
                    methodReturnTypeCache.put(methodId, dealReturnType(((Select)annotation).returnType()));
                }else if (annotation.annotationType().equals(Update.class)){
                    methodSqlCache.put(methodId, ((Update)annotation).value());
                    methodReturnTypeCache.put(methodId, dealReturnType(((Update)annotation).returnType()));
                }else if (annotation.annotationType().equals(Insert.class)){
                    methodSqlCache.put(methodId, ((Insert)annotation).value());
                    methodReturnTypeCache.put(methodId, dealReturnType(((Insert)annotation).returnType()));
                }else if (annotation.annotationType().equals(Delete.class)){
                    methodSqlCache.put(methodId, ((Delete)annotation).value());
                    methodReturnTypeCache.put(methodId, dealReturnType(((Delete)annotation).returnType()));
                }else {
                    throw new SQLException("unknown sql type " + annotation.annotationType());
                }
            }
        }
    }

    public Class<?> dealReturnType(String type){
        /* 8种基本类型无法通过Class.forName 反射获得*/
        String baseType = "int,long,float,double,char,byte,short,boolean,string";
        Class<?> result = null;
        try {
            if (baseType.contains(type.toLowerCase())){
                switch (type.toLowerCase()){
                    case "int":
                        result =  int.class;
                        break;
                    case "float":
                        result = float.class;
                        break;
                    case "long":
                        result = long.class;
                        break;
                    case "double":
                        result = double.class;
                        break;
                    case "byte":
                        result = byte.class;
                        break;
                    case "char":
                        result = char.class;
                        break;
                    case "boolean":
                        result = boolean.class;
                        break;
                    case "short":
                        result = short.class;
                        break;
                    case "string":
                        result = String.class;
                        break;
                    default:
                        throw new NoClassDefFoundError(type);
                }
            }else {
                result =  Class.forName(type);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}

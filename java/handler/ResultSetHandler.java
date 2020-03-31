package handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class ResultSetHandler {
    /**
     * 这是一种resultType的处理方法，没有从 .mapper文件中取resultMap中的映射，
     * 而是直接用 get + pojo 字段 的形式获取方法，然后赋值
     * @param resultSet
     * @param type
     * @param
     * @return
     */
    public Object handle(ResultSet resultSet, Class type, Boolean isList){
        Object pojo = null;
        List<Object> list = new LinkedList<>();
        try {
            while (resultSet.next()){
                pojo = type.newInstance();
                if (null != pojo ){
                    for (Field field : type.getDeclaredFields()){
                        setValue(pojo, field, resultSet);
                    }
                    list.add(pojo);
                }
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        if (isList){
            return list;
        }
        return pojo;
    }

    private void setValue(Object pojo, Field field, ResultSet rs) {
        try {
            Method setMethod = pojo.getClass().getMethod("set" + firstWordCaptital(field.getName()), field.getType());
            setMethod.invoke(pojo, getFieldValue(field, rs));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Object getFieldValue(Field field, ResultSet rs) {
        Class type = field.getType();
        Boolean isUnderLine = true; //是下划线命名法
        Boolean isCamel = true;      //是驼峰命名法
        Object result = null;
        String name = field.getName();
        try {
            while (true == isCamel || true == isUnderLine){
                if (int.class == type){
                    result = rs.getInt(name);
                }else if (long.class == type){
                    result = rs.getLong(name);
                }else if (double.class == type){
                    result = rs.getDouble(name);
                }else if (float.class == type){
                    result = rs.getFloat(name);
                }else if (boolean.class == type){
                    result = rs.getBoolean(name);
                }else {
                    result = rs.getString(name);
                }

                if (null != result){
                    return result;
                }else {
                    if (true == isCamel){
                        isCamel = false;
                        name = CamelToUnderLine(field.getName());
                        continue;
                    }else if (true == isUnderLine){
                        isUnderLine = false;
                    }else {
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 驼峰转下划线
     * @param name
     * @return
     */
    private String CamelToUnderLine(String name) {
        StringBuilder builder = new StringBuilder(name);
        int count = 0;
        //这里i = 1 为了跳过首字母大写的情况
        for (int i = 1; i < name.length(); ++i){
            if (Character.isUpperCase(name.charAt(i))){
                builder.insert(i + count, '_');
                ++count;
            }
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 首字母大写
     * @param name
     * @return
     */
    private String firstWordCaptital(String name) {
        try {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return null;
    }
}

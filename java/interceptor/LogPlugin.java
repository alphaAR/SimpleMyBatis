package interceptor;

import plugin.BasePlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LogPlugin extends BasePlugin {
    @Override
    public Object intercept(Object obj, Method method, Object[] args) {
        //intercept方法， 就是在执行原有逻辑之外，加入一些别的处理

        //1. 先将信息打印日志
        String statement = null;
        Object[] params = null;
        if (args.length >= 2){
            statement = (String)args[0];
            params = (Object[]) args[1];
        }
        System.out.println("执行的SQL是 " + statement + params);

        //2. 再执行原有业务逻辑
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}

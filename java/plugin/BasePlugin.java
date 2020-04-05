package plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class BasePlugin implements Plugin {
    @Override
    public Object getProxy(Object target) {
        Class<?> clazz = target.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz.getInterfaces()[0]}, new PluginHandler(target,this));
    }

    public abstract Object intercept(Object obj, Method method, Object[] args);
}

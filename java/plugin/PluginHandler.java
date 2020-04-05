package plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PluginHandler implements InvocationHandler {
    private Object target;  //所代理的对象
    private Plugin plugin;

    public PluginHandler(Object target, Plugin plugin) {
        this.plugin = plugin;
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return plugin.intercept(target, method, args);
    }
}

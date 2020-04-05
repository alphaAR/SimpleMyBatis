package plugin;

import java.lang.reflect.Method;

public interface Plugin {
    Object getProxy(Object target);

    Object intercept(Object obj, Method method, Object[] args);
}

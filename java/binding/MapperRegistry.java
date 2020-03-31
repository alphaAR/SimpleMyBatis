package binding;

import session.SqlSession;

import java.util.HashMap;
import java.util.Map;

public class MapperRegistry {
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public <T> void addMapper(Class<T> type){
        knownMappers.put(type, new MapperProxyFactory<>(type));
    }

    public <T> T getMapper(Class<T> type, SqlSession session){
        /* 这里后续可以配置是否延迟加载，如果否的话，就可以在扫描注解的时候直接添加 */
        MapperProxyFactory<T> factory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (null == factory){
            addMapper(type);
            return getMapper(type, session);
        }
        return (T) factory.newInstance(session);
    }
}

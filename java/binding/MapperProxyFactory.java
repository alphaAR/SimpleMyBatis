package binding;

import session.SqlSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {
    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession session){
        return (T) Proxy.newProxyInstance(this.mapperInterface.getClassLoader(),
                new Class[]{this.mapperInterface},
                new MapperProxy(session));
    }
}

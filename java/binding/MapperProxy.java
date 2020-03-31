package binding;

import session.SqlSession;
import session.MapperMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperProxy implements InvocationHandler {
    private SqlSession session;

    public MapperProxy(SqlSession session) {
        this.session = session;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodId = method.getDeclaringClass().getName() + "." + method.getName();
        if (this.session.getConfiguration().getMethodSqlCache().containsKey(methodId)){
            System.out.println(methodId);
            return new MapperMethod(this.session.getConfiguration(), method).execute(this.session, args);
        }
        return method.invoke(proxy, args);
    }
}

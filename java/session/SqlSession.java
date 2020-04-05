package session;

import executors.Executor;

import java.util.List;

public class SqlSession {
    private Configuration configuration;
    private Executor executor;

    public SqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = this.configuration.getPluginExecutor(this.configuration.getExecutor());
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public <T> T getMapper(Class<T> clazz){
        return configuration.getMapper(clazz, this);
    }

    public <T> T select(String statement, Object[] args, Class<?> pojo){
        //将这些重载方法封装一层，内部的转换统一到executor中去做
        return executor.query(statement, args, pojo);
    }

    public <T> T selectOne(String statement, Object[] args, Class<?> pojo){
        return executor.query(statement, args, pojo);
    }

    public <E> List<E> selectList(String statement, Object[] args, Class<?> pojo){
        return executor.queryList(statement, args, pojo);
    }

    public <T> T selectCursor(String statement, Object[] args){
        return null;
    }

    public int update(String statement, Object[] args){
        return executor.update(statement, args);
    }

    public int insert(String statement, Object[] args){
        return executor.update(statement, args);
    }

    public int delete(String statement, Object[] args){
        return executor.update(statement, args);
    }
}

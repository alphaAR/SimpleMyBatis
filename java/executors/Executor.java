package executors;

import java.util.List;

public interface Executor {
    <T> T query(String statement, Object[] args, Class pojo);

    <T> List<T> queryList(String statement, Object[] args, Class pojo);

    int update(String statement, Object[] args);
}

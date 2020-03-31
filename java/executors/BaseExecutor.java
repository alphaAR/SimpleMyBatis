package executors;

import Cache.CacheKey;
import handler.ParameterHandler;
import handler.ResultSetHandler;
import session.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseExecutor implements Executor {
    protected final Configuration configuration;
    protected final String url;
    protected final String user;
    protected final String password;
    protected final String driver;
    protected final Map<Integer, Object> FirstLevelCache = new HashMap<>();
    protected boolean shouldClearnCache;

    protected BaseExecutor(Configuration configuration) {
        this.configuration = configuration;
        url = configuration.getPropertyMap().getString("url");
        user = configuration.getPropertyMap().getString("user");
        password = configuration.getPropertyMap().getString("password");
        driver = configuration.getPropertyMap().getString("driver");
    }

    protected void closeResource(PreparedStatement preparedStatement, Connection conn){
        if (null != preparedStatement){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != conn){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName(this.driver);
            conn = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    @Override
    public <T> T query(String statement, Object[] args, Class pojo) {
        return (T) doQuery(statement, args, pojo, false);
    }

    @Override
    public <T> List<T> queryList(String statement, Object[] args, Class pojo) {
        return (List<T>) doQuery(statement, args, pojo, true);
    }

    @Override
    public int update(String statement, Object[] args) {
        return (int) doQuery(statement, args, null, false);
    }

    /**
     * @param statement
     * @param args
     * @param pojo
     * @param isList  是否返回List<E>
     * @return
     */
    public Object doQuery(String statement, Object[] args, Class pojo, Boolean isList){
        Connection conn = null;
        PreparedStatement pstmt = null;
        Object result = null;
        CacheKey cacheKey = new CacheKey();
        ResultSetHandler resultSetHandler = new ResultSetHandler();
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(statement);
            ParameterHandler parameterHandler = new ParameterHandler(pstmt);
            parameterHandler.setParameters(args);

            /*update/delete/insert时 pojo=null， 返回处理条数 */
            if (null == pojo){
                /* 当执行update/delete/insert时需要清理一级缓存 */
                shouldClearnCache = true;
                return pstmt.executeUpdate();
            }

            //select用ResultHandler处理数据
            if (true == shouldClearnCache){
                clearnFirstLevelCache();
            }

            buildCacheKey(cacheKey, statement, args);

            //先查一级缓存
            if (FirstLevelCache.containsKey(cacheKey.getCode())){
                result = FirstLevelCache.get(cacheKey.getCode());
                System.out.println("从一级缓存中查出 " + result);
            }else {
                shouldClearnCache = false;
                pstmt.execute();
                result = resultSetHandler.handle(pstmt.getResultSet(), pojo, isList);
                //放入一级缓存
                FirstLevelCache.put(cacheKey.getCode(), result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeResource(pstmt, conn);
        }
        return result;
    }

    /**
     * 组建CacheKey
     * @param cacheKey
     * @param statement
     * @param args
     */
    private void buildCacheKey(CacheKey cacheKey, String statement, Object[] args) {
        cacheKey.update(statement);
        if (null == args){
            cacheKey.update(null);
        }else {
            for (Object o : args){
                cacheKey.update(o);
            }
        }
    }

    protected void clearnFirstLevelCache() {
        FirstLevelCache.clear();
    }
}

package session;


import java.awt.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;

public class MapperMethod {
    private final SqlCommandType sqlType;
    private MethodSignature methodSignature;

    public MapperMethod(Configuration config, Method method) {
        try {
            this.methodSignature = new MethodSignature(method, config);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sqlType = this.methodSignature.getSqlType();
    }

    /**
     * 根据SQL命令的不同选择不同的方法
     * @return
     */
    public Object execute(SqlSession session, Object[] args){
        Object result = null;
        try {
            switch (this.sqlType){
                case UPDATE:
                    result = session.update(methodSignature.getStatement(), args);
                    break;
                case INSERT:
                    result = session.insert(methodSignature.getStatement(), args);
                    break;
                case DELETE:
                    result = session.delete(methodSignature.getStatement(), args);
                    break;
                case SELECT:
                    //根据query结果是一个还是多个，还要细分
                    if (methodSignature.returnMany){
                        result = session.selectList(methodSignature.getStatement(), args, this.methodSignature.getReturnType());
                    }else if (methodSignature.returnCursor){
                        System.out.println("select cursor.");
                    }else {
                        result = session.selectOne(methodSignature.getStatement(), args, this.methodSignature.getReturnType());
                    }
                    break;

                //FLUSH的情况是否还要分为commit/rollback的情况？
                case FLUSH:
                    break;
                case UNKNOWN:
                    throw new SQLException("unknown sql type " + SqlCommandType.UNKNOWN);
                default:
                    throw new SQLException("other sql type " + SqlCommandType.UNKNOWN);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 整理method的一些常用属性与方法
     */
    private class MethodSignature{
        private final Method method;
        private final String methodId;
        private final Configuration configuration;
        private final Class<?> returnType;
        private final boolean returnMany;
        private final boolean returnCursor;

        public MethodSignature(Method method, Configuration configuration) throws ClassNotFoundException {
            this.method = method;
            this.configuration = configuration;
            this.methodId = method.getDeclaringClass().getName() + "." + method.getName();

            /* method.getReturnType() 能够取出return类型，但若是list<E>时只能取出为list，而不知道E的类型，用它快速判断返回类型是否为多条，
            * 真正的returnType 还需要在mapper.xml 中进行配置或用注解配置 */
            Class defaultReturnType = this.method.getReturnType();
            this.returnMany = defaultReturnType.isArray() || isCollection(defaultReturnType);
            this.returnCursor = Cursor.class.equals(defaultReturnType);
            this.returnType = this.configuration.getMethodReturnTypeCache().get(this.methodId);
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public <T> boolean isCollection(Class<T> type) {
            return Collection.class.isAssignableFrom(type);
        }

        public SqlCommandType getSqlType() {
            SqlCommandType type = getSqlTypeFromCache();
            if (null == type){
                type = getSqlTypeByName(this.methodId);
                this.configuration.getSqlCommandTypeCache().put(this.methodId, type);
            }
            return type;
        }

        private SqlCommandType getSqlTypeByName(String methodName) {
            String sql = this.configuration.getMethodSqlCache().get(methodName);
            if (null == sql || sql.isEmpty()){
                return SqlCommandType.UNKNOWN;
            }

            sql = sql.trim();
            int index = sql.indexOf(" ");
            if (-1 == index){
                return SqlCommandType.UNKNOWN;
            }
            String firstWord = sql.substring(0, index).toUpperCase();

            switch (firstWord){
                case "SELECT":
                    return SqlCommandType.SELECT;
                case "UPDATE":
                    return SqlCommandType.UPDATE;
                case "INSERT":
                    return SqlCommandType.INSERT;
                case "DELETE":
                    return SqlCommandType.DELETE;
                case "FLUSH":
                    return SqlCommandType.FLUSH;
                default:
                    return SqlCommandType.UNKNOWN;
            }
        }

        private SqlCommandType getSqlTypeFromCache() {
            return this.configuration.getSqlCommandTypeCache().get(this.methodId);
        }

        public String getStatement() throws SQLException {
            String stmt = this.configuration.getMethodSqlCache().get(this.methodId);
            if (null == stmt){
                throw new SQLException(this.methodId + " hasn't config statement.");
            }
            return stmt;
        }
    }
}

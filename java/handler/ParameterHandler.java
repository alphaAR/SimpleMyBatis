package handler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParameterHandler {
    private PreparedStatement preparedStatement;
    public ParameterHandler(PreparedStatement pstmt) {
        this.preparedStatement = pstmt;
    }

    public void setParameters(Object[] args) {
        //后续应扩展， 加入参数支持parameterType（即对象类型）
        if (null == args){
            return;
        }

        try {
            for (int i = 0, len = args.length; i < len; ++i){
                int k = i + 1;
                if (args[i] instanceof Integer){
                    this.preparedStatement.setInt(k, (Integer)args[i]);
                }else if (args[i] instanceof Long){
                    this.preparedStatement.setLong(k, (Long)args[i]);
                }else if (args[i] instanceof Double){
                    this.preparedStatement.setDouble(k, (Double)args[i]);
                }else if (args[i] instanceof Float){
                    this.preparedStatement.setFloat(k, (Float)args[i]);
                }else if (args[i] instanceof Boolean){
                    this.preparedStatement.setBoolean(k, (Boolean)args[i]);
                }else {
                    this.preparedStatement.setString(k, String.valueOf(args[i]));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

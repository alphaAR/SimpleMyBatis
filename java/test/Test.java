package test;


import Mapper.EmpInfo;
import Mapper.EmpMapper;
import session.Configuration;
import session.SqlSession;

import java.util.LinkedList;
import java.util.List;

public class Test {
    private static List<String> classPaths = new LinkedList<>();
    private static List<Class<?>> mapperList = new LinkedList<>();

    public static void main(String[] args) {
/*
        SqlSession session = new SqlSession(new Configuration());
        if (null == session) {
            System.out.println("session is null!");
            return;
        }

        EmpMapper empMapper = session.getMapper(EmpMapper.class);
        if (null == empMapper) {
            System.out.println("empMapper is null!");
            return;
        }

        int count = empMapper.insertEmp("Jerry", 22, 30000);
        System.out.println(count);

//        empMapper.deleteById(22);

        List<EmpInfo> list = empMapper.selectAll();
        for (EmpInfo info : list) {
            System.out.println(info);
        }

        list = empMapper.selectAll();
        for (EmpInfo info : list) {
            System.out.println(info);
        }

        EmpInfo empInfo = empMapper.selectById(5);
        System.out.println(empInfo);
        empMapper.updateEmp("赵六", 31, 35000.0, 5);
        System.out.println(empMapper.selectById(5));
*/





    }


}

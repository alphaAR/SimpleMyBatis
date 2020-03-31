package Mapper;

import annotation.*;

import java.util.List;

@Mapper
public interface EmpMapper {
    @Insert(value = "insert into test.empinfo(name,age,salary) values(?,?,?)", returnType = "int")
    int insertEmp(String name, int age, double salary);

    @Delete(value = "delete from test.empinfo where id = ?", returnType = "int")
    int deleteById(int id);

    @Update(value = "update test.empinfo set name = ?, age = ?, salary = ? where id = ?", returnType = "int")
    int updateEmp(String name, int age, double salary, int id);

    @Select(value = "select id,name,age,salary from test.empinfo where id = ?", returnType="Mapper.EmpInfo")
    EmpInfo selectById(int id);

    @Select(value = "select id,name,age,salary from test.empinfo", returnType = "Mapper.EmpInfo")
    List<EmpInfo> selectAll();
}

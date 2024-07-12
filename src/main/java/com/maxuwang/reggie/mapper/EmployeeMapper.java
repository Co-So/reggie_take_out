package com.maxuwang.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maxuwang.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {// BaseMapper接口是mybatis-plus里面的，它提供了数据对数据库的一些增删改查等操作，其中还需要提供泛型类型，让其知道数据数据封装到那个实体中，这里需要提供的是Employee
}

package com.maxuwang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxuwang.reggie.entity.Employee;
import com.maxuwang.reggie.mapper.EmployeeMapper;
import com.maxuwang.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
/*
    这里继承的ServiceImpl<EmployeeMapper,Employee>也是由mybatis-plus提供的，
    其中EmployeeMapper泛型是上面创建的接口：告诉mybatis-plus该类需要使用EmployeeMapper
    Employee泛型是实体：告诉mybatis-plus需要将数据封装为Employee对象

    这里实现的EmployeeService为什么没有重写里面的方法呢？
        原因是因为这里继承了ServiceImpl类，这个类就已经实现了对应的方法，如果不继承该类，方法的实现还是需要自己来写的
*/

}

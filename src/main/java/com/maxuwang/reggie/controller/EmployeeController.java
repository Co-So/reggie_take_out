package com.maxuwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maxuwang.reggie.common.R;
import com.maxuwang.reggie.entity.Employee;
import com.maxuwang.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工处理
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    // 这个方法中HttpServletRequest的目的是保存已经登录的的用户，保存在服务端的request中
    // 登录校验就可以直接根据request来判断是否已经登录
    // 从这里定义的request来看，其实在springboot容器中除了你得到请求数据，其他请求对象也都可以拿到
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 根据前面的登录流程来写

        // 1. 将提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());// 这个是由springboot提供的md5加密工具

        // 2. 根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();// “Lambda查询包装”，由mybatis-plus提供
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果没有查到数据则返回登录失败
        if (emp == null){
            return R.error("账号不存在");
        }

        // 4. 密码比对，不一致则返回登录失败
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // 5. 查看员工状态
        if (emp.getStatus() == 0){
            return R.error("该账号已禁用");
        }

        // 6. 登录成功，将登录员工的ID存入request的Session中。以及 封装登录成功数据并返回
        request.getSession().setAttribute("employee", emp.getId());
        log.info("ID为 {} 的员工登录成功", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        log.info("ID为 {} 的员工退出...", request.getSession().getAttribute("employee"));
        // 删除Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     * @return
     */
    @PostMapping()// 这里本来是“/employee”请求路径，但是在类上面就是这个路径，所以这里就不用添加了，添加员工请求到来回默认调用方法
    public R<String> createEmp(HttpServletRequest request, @RequestBody Employee employee){

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));// 添加初始默认密码，需要进行md5加密


        employeeService.save(employee);
        

        return R.success("添加员工成功。");
    }

    /**
     * 员工分页查询
     * @returncategory
     */
    @GetMapping("/page")
    public R<Page>  page(Integer page, Integer pageSize, String name){

        // 设置分页对象
        Page<Employee> employeePage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 如果有用户的情况下，根据用户名过滤
        queryWrapper.like(StringUtils.isNotBlank(name), Employee::getName, name);
        // 添加排序条件，根据更新时间查询
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询，注意这里执行查询后会将结果封装在employeePage里面
        employeeService.page(employeePage, queryWrapper);

        return R.success(employeePage);
    }

    /**
     * 根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {

        // 执行修改操作
        employeeService.updateById(employee);

        return R.success("修改员工成功");
    }

    /**
     * 根据员工ID查询
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public R<Employee> getEmployeeById(@PathVariable Long userId){

        log.info("用户ID：{}", userId);

        Employee one = employeeService.getById(userId);

        log.info("用户：{}", one);
        return R.success(one);
    }
}

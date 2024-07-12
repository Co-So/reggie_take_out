package com.maxuwang.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maxuwang.reggie.common.R;
import com.maxuwang.reggie.entity.User;
import com.maxuwang.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户操作
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户登录
     * @param request HttpServletRequest对象
     * @param user 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody User user) {

        log.info("获得登录用户：{}", user);

        // 根据手机号查询用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getPhone, user.getPhone());
        User us = userService.getOne(userWrapper);

        // 如果用户不存在，插入新用户并设置状态为启用
        if (us == null) {
            user.setStatus(1);
            userService.save(user);
            us = user; // 新注册的用户信息
            log.info("注册新用户：{}", us);
        }

        // 检查用户是否已禁用
        if (us.getStatus() != 1) {
            return R.error("账号已禁用");
        }

        // 将用户ID存入会话
        request.getSession().setAttribute("user", us.getId());

        return R.success(us);
    }



}

package com.maxuwang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxuwang.reggie.entity.User;
import com.maxuwang.reggie.mapper.UserMapper;
import com.maxuwang.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

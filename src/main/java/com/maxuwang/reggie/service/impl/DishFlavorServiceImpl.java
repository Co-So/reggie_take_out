package com.maxuwang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxuwang.reggie.entity.DishFlavor;
import com.maxuwang.reggie.mapper.DishFlavorMapper;
import com.maxuwang.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}

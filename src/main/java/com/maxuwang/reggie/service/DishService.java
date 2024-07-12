package com.maxuwang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maxuwang.reggie.dto.DishDto;
import com.maxuwang.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据，需要操作，dish，dish_flavor两张表
    public void saveWithFlavor(DishDto dishDto);

    // 根据ID查询菜品，以及该菜品的口味
    public DishDto getByIdWithFlavor(Long id);

    // 修改菜品，以及菜品对应的口味
    public void updateWithFlavor(DishDto dishDto);

    // 删除菜品，以及菜品对应的口味
    void removeWithFlavor(Long[] ids);
}

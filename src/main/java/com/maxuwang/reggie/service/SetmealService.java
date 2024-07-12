package com.maxuwang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.maxuwang.reggie.dto.SetmealDto;
import com.maxuwang.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    // 新增套餐，以及套餐下的菜品与套餐的关系
    void createWithSetmealDish(SetmealDto setmealDto);

    // 根据套餐id查询套餐，以及该套餐下的菜品，以及改套餐的分类
    SetmealDto getByIdWithSetmealDish(Long id);

    // 修改套餐的基本信息，并修改套餐下的菜品
    void updateWithDish(SetmealDto setmealDto);

    // 删除套餐，以及套餐下关联的菜品信息
    void removeWithDish(Long[] ids);
}

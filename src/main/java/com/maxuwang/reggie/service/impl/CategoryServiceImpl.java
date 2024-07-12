package com.maxuwang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxuwang.reggie.common.CustomException;
import com.maxuwang.reggie.entity.Category;
import com.maxuwang.reggie.entity.Dish;
import com.maxuwang.reggie.entity.Setmeal;
import com.maxuwang.reggie.mapper.CategoryMapper;
import com.maxuwang.reggie.service.CategoryService;
import com.maxuwang.reggie.service.DishService;
import com.maxuwang.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    // 注入菜品service
    @Autowired
    DishService dishService;

    // 注入套餐service
    @Autowired
    SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前判断该分类下面是否关联菜品或套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 菜品的判断
        // 查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);

        // 判断是否关联菜品，如果已经关联，抛出业务异常
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0){
            // 已关联菜品，抛出业务异常
            throw new CustomException("该分类下已有菜品，不能删除");
        }


        // 套餐的判断
        // 查询条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);

        // 判断是否关联套餐，如果已经关联，抛出业务异常
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0){
            // 已关联套餐，抛出业务异常
            throw new CustomException("该分类下已有套餐，不能删除");
        }

        // 删除分类，这里调用了ServiceImpl的removeById(~)方法来删除数据
        super.removeById(id);
    }
}

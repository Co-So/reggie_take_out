package com.maxuwang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxuwang.reggie.dto.SetmealDto;
import com.maxuwang.reggie.entity.Category;
import com.maxuwang.reggie.entity.Setmeal;
import com.maxuwang.reggie.entity.SetmealDish;
import com.maxuwang.reggie.mapper.SetmealMapper;
import com.maxuwang.reggie.service.CategoryService;
import com.maxuwang.reggie.service.SetmealDishService;
import com.maxuwang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 新增菜品
     * @param setmealDto
     */
    @Override
    @Transactional
    public void createWithSetmealDish(SetmealDto setmealDto) {
        // 插入套餐信息到数据库中
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 将套餐id放入setmealDish里面
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }

        // 将套餐与菜品的信息插入关联表
        setmealDishService.saveBatch(setmealDishes);
    }


    @Autowired
    CategoryService categoryService;
    /**
     * 根据套餐id查询套餐，并且查询该套餐下的菜品，以及该套餐的分类
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithSetmealDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();

        // 查询套餐信息
        Setmeal setmeal = this.getById(id);

        // 拷贝对象
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 查询菜品过滤器
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);

        // 查询该套餐下的菜品
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

        // 给返回值设置菜品的值
        setmealDto.setSetmealDishes(setmealDishes);

        // 查询套餐分类信息
        Category category = categoryService.getById(setmeal.getCategoryId());

        setmealDto.setCategoryName(category.getName());

        return setmealDto;
    }

    /**
     * 修改套餐，并修改套餐下的菜品
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        // 先根据套餐表里面的id删除套餐菜品关联表里面的菜品
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
        setmealDishWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishWrapper);


        Setmeal setmeal = new Setmeal();
        // 拷贝对象
        BeanUtils.copyProperties(setmealDto, setmeal);

        // 更新套餐基本信息
        this.updateById(setmeal);

        // 向套餐菜品关联表里面插入关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            // 给对应的套餐关系赋值
            item.setSetmealId(setmeal.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，以及套餐下关联的菜品信息
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(Long[] ids) {

        // 将数组转换为列表，方便使用
        List<Long> idList = Arrays.asList(ids);

        // 删除套餐菜品关联表中的关联信息
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
        setmealDishWrapper.in(SetmealDish::getSetmealId, idList);
        setmealDishService.remove(setmealDishWrapper);

        // 删除套餐基本信息
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.in(Setmeal::getId, idList);
        this.remove(setmealWrapper);
    }
}

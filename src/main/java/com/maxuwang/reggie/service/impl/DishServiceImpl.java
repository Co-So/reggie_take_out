package com.maxuwang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxuwang.reggie.common.R;
import com.maxuwang.reggie.dto.DishDto;
import com.maxuwang.reggie.entity.Dish;
import com.maxuwang.reggie.entity.DishFlavor;
import com.maxuwang.reggie.mapper.DishMapper;
import com.maxuwang.reggie.service.DishFlavorService;
import com.maxuwang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存菜品口味数据
     * @param dishDto
     */
    @Override
    @Transactional // 事务控制
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品信息
        this.save(dishDto);// 这里可以直接使用DishDto的原因是因为DishDto继承了Dish

        // 获取菜品ID
        Long dishId = dishDto.getId();

        // 用stream流的方式给菜品加上对应的口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据ID查询菜品和对于菜品的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        // 拷贝对象
        BeanUtils.copyProperties(dish, dishDto);

        log.info("根据ID {} 查询到菜品：{}", id, dish);

        // 口味条件构造器
        LambdaQueryWrapper<DishFlavor> dishFlavorWrapper = new LambdaQueryWrapper<>();

        // 添加条件
        dishFlavorWrapper.eq(DishFlavor::getDishId, dish.getId());

        // 查询口味
        List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorWrapper);

        log.info("查询到口味集合：{}", dishFlavors);

        // 如果需要将list加入到dishDto中，可以按需设置
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    /**
     * 修改菜品以及对于菜品的口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        // 修改菜品信息
        this.updateById(dishDto);

        // 修改菜品对应的口味信息，注意这里如果是将对应的口味信息检索出来，再通过对比的方式进行修改是比较麻烦的，这里直接删除原来的口味信息，然后再将新的口味信息存储起来即可
        // 口味条件构造器
        LambdaQueryWrapper<DishFlavor> dishFlavorWrapper = new LambdaQueryWrapper<>();
        // 添加条件
        dishFlavorWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        // 删除菜品对应的口味信息
        dishFlavorService.remove(dishFlavorWrapper);

        // 插入菜品对应的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 这里要对flavors的dish_id字段处理，视频讲解说需要给其赋值。但是我刚才没有写下面这段代码也正确的运行了
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品以及菜品对应的口味
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithFlavor(Long[] ids) {

        // 将数组转换为列表，方便使用
        List<Long> idList = Arrays.asList(ids);

        // 删除菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
        flavorQueryWrapper.in(DishFlavor::getDishId, idList);
        dishFlavorService.remove(flavorQueryWrapper);

        // 删除菜品信息
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(Dish::getId, idList);
        this.remove(dishQueryWrapper);
    }
}

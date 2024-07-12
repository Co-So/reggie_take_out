package com.maxuwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maxuwang.reggie.common.R;
import com.maxuwang.reggie.dto.SetmealDto;
import com.maxuwang.reggie.entity.Category;
import com.maxuwang.reggie.entity.Dish;
import com.maxuwang.reggie.entity.Setmeal;
import com.maxuwang.reggie.service.CategoryService;
import com.maxuwang.reggie.service.SetmealDishService;
import com.maxuwang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> createSetmeal(@RequestBody SetmealDto setmealDto) {

        log.info("套餐信息：{}", setmealDto);

        setmealService.createWithSetmealDish(setmealDto);

        return R.success("创建成功");
    }

    @Autowired
    CategoryService categoryService;

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        // 创建分页对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);

        // 创建用于返回的分页对象
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //  条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        // 添加模糊查询条件，如果提供了“模糊对象”
        queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);

        // 添加排序条件，根据更新时间查询与sort进行排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        // 查询数据库
        setmealService.page(setmealPage, queryWrapper);

        // 对象拷贝，在前面“7.3.4 修改菜品”节有讲过拷贝对象
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        List<Setmeal> setmeals = setmealPage.getRecords();


        List<SetmealDto> setmealDtos  = setmeals.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            // 对象拷贝
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtos);

        return R.success(setmealDtoPage);

    }

    /**
     * 根据id获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> setmeal(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithSetmealDish(id);

        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){

        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功");
    }


    /**
     * 修改套餐售卖状态
     * @param st
     * @param ids
     * @return
     */
    @PostMapping("/status/{st}")
    public R<String> status(@PathVariable Integer st, Long[] ids){
        if (ids == null || ids.length == 0) {
            return R.error("没有提供要更新的ID");
        }

        // 创建一个要更新的实体列表
        List<Setmeal> setmeals = new ArrayList<>();
        for (Long id : ids) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(st);
            setmeals.add(setmeal);
        }

        // 批量更新
        boolean updateResult = setmealService.updateBatchById(setmeals);

        if (updateResult) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){

        setmealService.removeWithDish(ids);

        return R.success("删除套餐成功");
    }
}

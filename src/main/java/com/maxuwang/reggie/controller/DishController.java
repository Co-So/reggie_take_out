package com.maxuwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maxuwang.reggie.common.R;
import com.maxuwang.reggie.dto.DishDto;
import com.maxuwang.reggie.entity.Category;
import com.maxuwang.reggie.entity.Dish;
import com.maxuwang.reggie.service.CategoryService;
import com.maxuwang.reggie.service.DishFlavorService;
import com.maxuwang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> createDish(@RequestBody DishDto dishDto) {

        // 由于这里需要同时操作两个操作，业务逻辑比较复杂，所以交给业务层处理，所以需要在业务层自己新建一个方法用于处理该业务逻辑
        dishService.saveWithFlavor(dishDto);

        return R.success("操作成功");
    }


    @Autowired
    CategoryService categoryService;// 分类service，本节重点关注代码

    /**
     * 分类分页查询
     *
     * @returncategory
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {

        // 设置分页对象
        Page<Dish> dishPage = new Page<>(page, pageSize);

        // 创建用于返回的 DishDtoPage 对象
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 添加模糊查询条件，如果提供了“模糊对象”
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);

        // 添加排序条件，根据更新时间查询与sort进行排序
        queryWrapper.orderByAsc(Dish::getSort);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行查询，注意这里执行查询后会将结果封装在employeePage里面
        dishService.page(dishPage, queryWrapper);

        // 对象拷贝，将查询到的dishPage对象的数据拷贝给dishDtoPage对象
        // 这里需要拷贝需要忽略Page里面的records这个属性，Page里面的records属性主要是存储的分页所需要的具体数据的集合，例如这里的：菜品名称，图片。像查询多少条数据，显示的条数，当前页码等信息存储在其他的属性里面
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");// 第一个参数：拷贝里面的值；第二个参数：拷贝到该对象里面；第三个参数：需要忽略的参数
        // 处理分类名称赋值，得到类似菜品名称，图片等的集合信息
        List<Dish> records = dishPage.getRecords();

        List<DishDto> dishDtos = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            // 将Dish（item）里面已有的值拷贝给DishDto
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();// 得到分类ID
            Category category = categoryService.getById(categoryId);// 分类对象
            if (category != null) {// 如果查带对应的分类则不需要赋值
                String categoryName = category.getName();// 分类名称
                // 给dishDto对象的categoryName赋值
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        // 将拷贝后的dto集合放到dishDtoPage分页对象里面
        dishDtoPage.setRecords(dishDtos);

        return R.success(dishDtoPage);
    }

    /**
     * 根据菜品ID查询
     *
     * @param dishId
     * @return
     */
    @GetMapping("/{dishId}")
    public R<DishDto> dish(@PathVariable Long dishId) {

        DishDto dishDto = dishService.getByIdWithFlavor(dishId);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        log.info("收到dishDto = {}", dishDto);

        dishService.updateWithFlavor(dishDto);

        return R.success("修改成功");
    }

    /**
     * 修改菜品售卖状态
     * @param st
     * @param ids
     * @return
     */
    @PostMapping("/status/{st}")
    public R<String> status(@PathVariable Integer st, Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.error("没有提供要更新的ID");
        }

        // 创建一个要更新的实体列表
        List<Dish> dishes = new ArrayList<>();
        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(st);
            dishes.add(dish);
        }

        // 批量更新
        boolean updateResult = dishService.updateBatchById(dishes);

        if (updateResult) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    /**
     * 根据id删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){

        dishService.removeWithFlavor(ids);

        return R.success("删除成功");
    }

    /**
     * 根据分类ID查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Long categoryId){
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, categoryId);

        List<Dish> list = dishService.list(dishWrapper);

        return R.success(list);
    }
}
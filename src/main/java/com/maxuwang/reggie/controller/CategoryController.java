package com.maxuwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maxuwang.reggie.common.R;
import com.maxuwang.reggie.entity.Category;
import com.maxuwang.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 新增菜品类型和新增套餐类型
     * @param category
     * @return
     */
    @PostMapping
    public R<String> createCategory(@RequestBody Category category){

        categoryService.save(category);

        return R.success("新增分类成功");
    }

    /**
     * 分类分页查询
     * @returncategory
     */
    @GetMapping("/page")
    public R<Page>  page(Integer page, Integer pageSize){

        // 设置分页对象
        Page<Category> categoryPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        // 添加排序条件，根据更新时间查询与sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        queryWrapper.orderByDesc(Category::getUpdateTime);

        // 执行查询，注意这里执行查询后会将结果封装在employeePage里面
        categoryService.page(categoryPage, queryWrapper);

        return R.success(categoryPage);
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {

        // 执行修改操作
        categoryService.updateById(category);

        return R.success("修改分类成功");
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){

        log.info("删除分类id：{}", ids);

        categoryService.remove(ids);

        return R.success("删除成功");
    }

    /**
     * 根据条件查询分类信息
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);// 添加排序

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}

package com.maxuwang.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐菜品关系
 */
@Data
public class SetmealDish implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    //套餐id
    private Long setmealId;
    //菜品id
    private Long dishId;
    //菜品名称（冗余字段，有了该属性可以避免多次查询数据库，减少开销）
    private String name;
    //菜品原价（冗余字段，有了该属性可以避免多次查询数据库，减少开销）
    private BigDecimal price;
    //份数
    private Integer copies;
    //排序
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
    //是否删除
    private Integer isDeleted;
}

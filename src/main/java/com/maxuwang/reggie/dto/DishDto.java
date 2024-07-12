package com.maxuwang.reggie.dto;

import com.maxuwang.reggie.entity.Dish;
import com.maxuwang.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    List<DishFlavor> flavors = new ArrayList<>();// 口味
    private  String categoryName;// 后面会有所讲解
    private Integer copies;// 后面会有所讲解
}

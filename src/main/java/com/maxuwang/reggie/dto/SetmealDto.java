package com.maxuwang.reggie.dto;

import com.maxuwang.reggie.entity.Setmeal;
import com.maxuwang.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

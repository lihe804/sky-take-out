package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增菜品口味
     * @param flavors
     */
    void insertFlavor(List<DishFlavor> flavors);

   /* @Insert("insert into dish_flavor (dish_id, name, value) values (#{dishId}, #{name}, #{value})")
    void insertFlavor(DishFlavor flavor);*/

}

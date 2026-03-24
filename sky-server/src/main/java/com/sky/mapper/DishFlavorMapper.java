package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增菜品口味
     * @param flavors
     */
    void insertFlavor(List<DishFlavor> flavors);

    /**
     * 批量删除菜品口味
     * @param ids
     */
    void deleteDishFlavorById(List<Long> ids);

     /**
      * 根据菜品id查询菜品口味
      * @param id
      * @return
      */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);

   /* @Insert("insert into dish_flavor (dish_id, name, value) values (#{dishId}, #{name}, #{value})")
    void insertFlavor(DishFlavor flavor);*/

}

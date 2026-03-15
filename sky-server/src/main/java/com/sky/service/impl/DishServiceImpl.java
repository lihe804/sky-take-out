package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        List<Dish> dishList = dishMapper.list(dishPageQueryDTO);
        //2.获取分页信息
        Page<Dish> page = (Page<Dish>) dishList;
        return new PageResult(page.getTotal(), page.getResult());

    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(DishDTO dishDTO) {
        // 1. 创建菜品对象并复制属性
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        
        // 2. 插入菜品基本信息，获取生成的 ID
        dishMapper.insert(dish);

        //3.获取insert语句生成的主键值
        Long dishId = dish.getId();

        // 4. 处理口味数据
       /* List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
                dishFlavorMapper.insertFlavor(flavor);
            });
        }*/

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertFlavor(flavors);
        }
    }
}

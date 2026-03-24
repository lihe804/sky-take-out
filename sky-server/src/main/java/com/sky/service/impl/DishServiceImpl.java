package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        List<Dish> dishList = dishMapper.list(dishPageQueryDTO);
        
        //2.将 Dish 转换为 DishVO
        List<DishVO> dishVOList = new ArrayList<>();
        dishList.forEach(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVOList.add(dishVO);
        });
        dishVOList.forEach(dishVO -> {
            Category category = categoryMapper.getById(dishVO.getCategoryId());
            dishVO.setCategoryName(category.getName());
        });

        //3.获取分页信息
        Page<Dish> page = (Page<Dish>) dishList;

        return new PageResult(page.getTotal(), dishVOList);
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

    /**
    * 批量删除菜品
     *@param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> ids) {
        //1.根据菜品id删除菜品基本信息
        dishMapper.deleteDishById(ids);
        //2.根据菜品id删除菜品口味信息
        dishFlavorMapper.deleteDishFlavorById(ids);
    }

    /**
     * 根据id查询菜品和口味信息
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        //3.根据菜品id查询菜品口味信息
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //4.将查询到的菜品信息和口味信息封装到DishVO对象中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        Category category = categoryMapper.getById(dish.getCategoryId());
        dishVO.setCategoryName(category.getName());
        return dishVO;
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
    List<Dish> dishList =dishMapper.listByCategoryId(categoryId);
    return dishList;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(DishDTO dishDTO) {
        // 1. 更新菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 2. 删除原有的口味数据
        List<Long> ids = new ArrayList<>();
        ids.add(dishDTO.getId());
        dishFlavorMapper.deleteDishFlavorById(ids);

        // 3. 处理新的口味数据

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertFlavor(flavors);
        }
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Dish dish=dishMapper.getById(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }


}

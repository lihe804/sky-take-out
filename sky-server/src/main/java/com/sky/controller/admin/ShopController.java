package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    private final String key = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺状态
     * @param status 店铺状态，1：营业中，0：打样
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation(value = "设置店铺状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺状态为：{}",status==1 ? "营业中" : "打样");
        //将店铺状态写入 Redis 中
        redisTemplate.opsForValue().set(key,status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺状态")
    public Result getStatus(){
        //从 Redis 中获取店铺状态
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        log.info("店铺状态为：{}",status==1 ? "营业中" : "打样");
        return Result.success(status);
    }
}

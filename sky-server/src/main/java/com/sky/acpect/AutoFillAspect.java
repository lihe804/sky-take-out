package com.sky.acpect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 切面类，用于处理自动填充功能字段
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {
        log.info("自动填充功能字段");
    }

    /**
     * 前置通知，在目标方法执行前自动填充功能字段
     * @param joinPoint 连接点对象，用于获取目标方法的信息
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
         log.info("自动填充功能字段");
         //接收当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的 AutoFill 注解
        OperationType operationType = autoFill.value();//获取注解上的操作类型

        //获取当前被拦截方法的参数--实体对象
        Object[] args=joinPoint.getArgs();
        if (args==null || args.length==0) {
            return;
        }
        //获取实体对象
        Object entity = args[0];
        LocalDateTime localDateTime = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据数据库操作类型，为对应的属性赋值
        if(operationType==OperationType.INSERT){
         try {
                //获取实体对象上的 setCreateTime 方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                //设置方法为可访问
                setCreateTime.setAccessible(true);
                //调用方法，填充创建时间 invoke:调用方法，填充创建时间
                setCreateTime.invoke(entity, localDateTime);

                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.setAccessible(true);
                setUpdateTime.invoke(entity, localDateTime);

                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                setCreateUser.setAccessible(true);
                setCreateUser.invoke(entity, currentId);

                Method setUpdateUser= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUser.setAccessible(true);
                setUpdateUser.invoke(entity, currentId);
            }catch (Exception e) {
                log.error("自动填充创建时间和更新时间失败", e);
            }

        }else if(operationType==OperationType.UPDATE){
         try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.setAccessible(true);
                setUpdateTime.invoke(entity, localDateTime);

                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUser.setAccessible(true);
                setUpdateUser.invoke(entity, currentId);
            }catch (Exception e) {
                log.error("自动填充更新时间和更新人失败", e);
            }
        }
    }

}



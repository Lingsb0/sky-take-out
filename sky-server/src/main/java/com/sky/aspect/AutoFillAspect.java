package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 面试题: 项目中哪里用到了AOP
 *        1) 字段自动填充:   aop 前置通知+ 注解
 *
 * 字段自动填充切面类
 */
@Aspect // 声明切面类
@Component // 放入sprnig 容器
@Slf4j
public class AutoFillAspect {

    //@Before("execution(* com.sky.mapper.*.*(..))") // 拦截某一层
    //@Before("@annotation(com.sky.annotation.AutoFill)") // 只要有这个注解都拦截
    // mapper 层 和注解 必须同时满足
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")
    public void pt(){}

    @Before("pt()")
    /*JoinPoint 是spring 把拦截到的方法进行了了封装的一个对象
    JoinPoint
          MethodSignature
                   Method ==== void insert(Employee employee);
                         annotation

    * */
    public void autoFill(JoinPoint jp) throws Exception{
        log.info("拦截到了请求....");

        // 1) 判断请求类型 是 insert 或update
        MethodSignature signature = (MethodSignature)jp.getSignature();
        Method method = signature.getMethod();// 获取拦截到的 方法对象 ,比如 insert /update
        AutoFill annotation = method.getAnnotation(AutoFill.class); // 获取方法上的注解
        OperationType operationType = annotation.value();

        // 2)  获取目标方法(insert update)的参数
        Object[] args = jp.getArgs();// 参数 有可能有多个,所以是数组
        if(args==null|| args.length==0){
            return;
        }
        Object param = args[0]; //  insert /update 的参数 ,对象不确定是啥

        //3) 通过反射方式调用目标对象的 setCreateTime 等方法
        if(operationType==OperationType.INSERT){
            // 反射获取对应的 set 方法
            Method setCreateTime = param.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = param.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUser = param.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = param.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            // 调用
            setCreateTime.invoke(param,LocalDateTime.now());// param.setCreateTime(LocalDateTime.now())
            setUpdateTime.invoke(param,LocalDateTime.now());//
            setCreateUser.invoke(param, BaseContext.getCurrentId());
            setUpdateUser.invoke(param, BaseContext.getCurrentId());

        }else if(operationType==OperationType.UPDATE){

            Method setUpdateTime = param.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = param.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);


            setUpdateTime.invoke(param,LocalDateTime.now());// param.setCreateTime(LocalDateTime.now())
            setUpdateUser.invoke(param, BaseContext.getCurrentId());
        }




    }

}
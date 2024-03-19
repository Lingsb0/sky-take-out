package com.sky.annotation;

import com.sky.enumeration.OperationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
@Target(ElementType.METHOD) // 只能加载方法上
public @interface AutoFill {
    //OperationType   name();
    OperationType    value(); // 使用value 因为使用者可以省略

}
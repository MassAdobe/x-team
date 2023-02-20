package com.x.team.schedule.springboot.starter.annotation;

import com.x.team.common.constants.CommonConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：定时任务注解
 *
 * @author 月关
 * @date Created in 2022/7/4 7:49 下午
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Schedule {

    /**
     * 暂无意义
     */
    String value() default CommonConstants.EMPTY;
}

package com.x.team.log.springboot.starter.service;

/**
 * 描述：调整日志级别
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:57
 */
public interface LogLevelService {

    /**
     * 调整日志级别
     */
    String resetLogLevel(String level);
}

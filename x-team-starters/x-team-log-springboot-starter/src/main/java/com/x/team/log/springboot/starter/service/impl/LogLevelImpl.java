package com.x.team.log.springboot.starter.service.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.x.team.log.springboot.starter.service.LogLevelService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 描述：调整日志级别
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:57
 */
@Slf4j
@Service
public class LogLevelImpl implements LogLevelService {

    /**
     * 调整日志级别
     * <p>
     * Trace:是追踪，就是程序推进以下，你就可以写个trace输出，所以trace应该会特别多，不过没关系，我们可以设置最低日志级别不让他输出.
     * <p>
     * Debug:指出细粒度信息事件对调试应用程序是非常有帮助的.
     * <p>
     * Info:消息在粗粒度级别上突出强调应用程序的运行过程.
     * <p>
     * Warn:输出警告及warn以下级别的日志.
     * <p>
     * Error:输出错误信息日志.
     * <p>
     * 此外OFF表示关闭全部日志，ALL表示开启全部日志。
     */
    @Override
    public String resetLogLevel(String level) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("com.x.team");
        log.info("【调整日志级别】: 当前日志级别: {}", logger.getLevel());
        logger.setLevel(Level.valueOf(level));
        log.info("【调整日志级别】: 修改后生效的日志级别: {}", logger.getLevel());
        return logger.getLevel().toString();
    }
}

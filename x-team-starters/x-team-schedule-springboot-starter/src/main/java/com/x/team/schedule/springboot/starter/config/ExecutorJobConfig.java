package com.x.team.schedule.springboot.starter.config;

import com.x.team.common.constants.CommonConstants;
import com.x.team.schedule.springboot.starter.constants.ScheduleConstants;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * 描述：XXL-JOB CONFIGURATION
 *
 * @author MassAdobe
 * @date Created in 2023/2/20 10:33
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = ExecutorJobConfig.EXECUTOR_JOB_CONFIG_PREFIX)
public class ExecutorJobConfig {

    public static final String EXECUTOR_JOB_CONFIG_PREFIX = "xxl.job.executor";

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 调度中心部署跟地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
     */
    private String adminAddresses;
    /**
     * 执行器通讯TOKEN [选填]：非空时启用；
     */
    private String accessToken;
    /**
     * 执行器注册 [选填]：优先使用该配置作为注册地址，为空时使用内嵌服务 ”IP:PORT“ 作为注册地址。从而更灵活的支持容器类型执行器动态IP和动态映射端口问题。
     */
    private String address = CommonConstants.EMPTY;
    /**
     * 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；
     */
    private static String ip;
    /**
     * 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
     */
    private static Integer port;
    /**
     * 日志路径
     */
    private String logPath;
    /**
     * 日志滚动时间
     */
    private Integer logRetentionDays;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info("[xxl-job-config]: xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(ScheduleConstants.SCHEDULE_LOG_PREFIX + this.applicationName);
        if (!StringUtils.isEmpty(address)) {
            xxlJobSpringExecutor.setAddress(address);
        } else if (!StringUtils.isEmpty(ExecutorJobConfig.ip)) {
            xxlJobSpringExecutor.setIp(ExecutorJobConfig.ip);
        }
        if (Objects.nonNull(ExecutorJobConfig.port) && 0 < ExecutorJobConfig.port) {
            xxlJobSpringExecutor.setPort(ExecutorJobConfig.port);
        }
        xxlJobSpringExecutor.setAccessToken(this.accessToken);
        xxlJobSpringExecutor.setLogPath(this.logPath);
        xxlJobSpringExecutor.setLogRetentionDays(this.logRetentionDays);
        return xxlJobSpringExecutor;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static String getIp() {
        return ExecutorJobConfig.ip;
    }

    public void setIp(String ip) {
        ExecutorJobConfig.ip = ip;
    }

    public static Integer getPort() {
        return ExecutorJobConfig.port;
    }

    @Value("${xxl.job.executor.port}")
    public void setPort(Integer port) {
        ExecutorJobConfig.port = port;
    }

    public String getLogPath() {
        return logPath;
    }

    @SuppressWarnings("all")
    public void setLogPath(String logPath) {
        File dir = new File(logPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.logPath = logPath;
    }

    public Integer getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(Integer logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }
}

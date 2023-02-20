package com.x.team.test.server;

import com.x.team.config.springboot.starter.utils.SpringbootStartUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 描述：启动类
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 10:25
 */
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.x.team"})
@MapperScan({"com.x.team.test.server.repository"})
@SpringBootApplication(scanBasePackages = {"com.x.team"})
public class TestApplication {

    public static void main(String[] args) {
        SpringbootStartUtils.settingLog();
        SpringApplication.run(TestApplication.class, args);
    }
}

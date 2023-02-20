package com.xxl.job.admin;

import com.xxl.job.core.util.SpringbootStartUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述：XXL-JOB-ADMIN-LAUNCH
 *
 * @author MassAdobe
 * @date Created in 2023/2/20 11:20
 */
@SpringBootApplication
public class XxlJobAdminApplication {

	public static void main(String[] args) {
		SpringbootStartUtils.settingLog();
        SpringApplication.run(XxlJobAdminApplication.class, args);
	}

}
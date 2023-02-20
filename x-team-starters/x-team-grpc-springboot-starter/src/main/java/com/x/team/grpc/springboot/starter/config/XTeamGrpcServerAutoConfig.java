package com.x.team.grpc.springboot.starter.config;

import com.x.team.grpc.springboot.starter.interceptor.HeaderServerInterceptor;
import com.x.team.grpc.springboot.starter.interceptor.ServerGrpcInterceptor;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：configuration of grpc server
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 10:07
 */
@Configuration
public class XTeamGrpcServerAutoConfig {

    @Autowired
    private ServerGrpcInterceptor serverGrpcInterceptor;

    @Autowired
    private HeaderServerInterceptor headerServerInterceptor;

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurerAdapter() {
        return registry -> {
            registry.add(this.serverGrpcInterceptor);
            registry.add(this.headerServerInterceptor);
        };
    }

}

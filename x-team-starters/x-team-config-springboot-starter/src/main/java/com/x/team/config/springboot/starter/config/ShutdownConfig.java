package com.x.team.config.springboot.starter.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述：gracefully shutdown
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 17:29
 */
@Slf4j
@Configuration
public class ShutdownConfig {

    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }

    private static class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

        private volatile Connector connector;
        private final int waitTime = 30;

        @Override
        public void customize(Connector connector) {
            this.connector = connector;
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            // self defined keep-alive timeout, when the time passed 30s, disconnect the connection
            protocol.setKeepAliveTimeout(30000);
            // when client sending requests beyond 10000, request connection automatically disconnect
            protocol.setMaxKeepAliveRequests(10000);
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
            log.info("【SYSTEM】: program is exiting, clear all resources");
            this.connector.pause();
            try {
                //tomcat http thread connection pool
                Executor executor = this.connector.getProtocolHandler().getExecutor();
                if (executor instanceof ThreadPoolExecutor) {
                    // let service offline from nacos
                    log.info("【SYSTEM】: service has offline from nacos");
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    log.info("【SYSTEM】: shutting down tomcat connection pool, current has {} active threads，{} has been queued", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
                    threadPoolExecutor.shutdown();
                    log.info("【SYSTEM】: program shutdown down thread pool, current has {} active threads，{} has been queued", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
                    if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
                        log.info("【SYSTEM】: program shutdown failed, current has {} active threads，{} has been queued", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
                        log.info("【SYSTEM】：Tomcat thread can't be shutdown in {}s , attempt to force shutdown", waitTime);
                    }
                    // after 10 second, shutdown whole program, let all resources lease
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                log.warn("【SYSTEM】: tomcat thread pool shutdown failed");
                Thread.currentThread().interrupt();
            }
            log.info("【SYSTEM】: release all resources success");
        }
    }

}

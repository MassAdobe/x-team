package com.x.team.rest.springboot.starter.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.team.common.constants.HttpConstants;
import com.x.team.rest.springboot.starter.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 描述：routing configuration
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 17:57
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * no need to validate token (system)
     */
    public static String[] contextExcludeUrls = new String[]{
            "/webjars/**",
            "/configuration/ui",
            "/actuator/**",
            "/**/warmup",
            "/**/map/**",
            "/**/callback/**"
    };

    @Bean
    public TokenInterceptor tokenInterceptor() {
        return new TokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry
                .addInterceptor(tokenInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(contextExcludeUrls);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        resourceHandlerRegistry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public MappingJackson2HttpMessageConverter responseBodyConverter() {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return new MappingJackson2HttpMessageConverter(OBJECT_MAPPER);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(responseBodyConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    /**
     * cors configuration methods
     */
    private static final String[] ORIGINS = new String[]{
            HttpConstants.HTTP_REQUEST_METHOD_GET,
            HttpConstants.HTTP_REQUEST_METHOD_POST,
            HttpConstants.HTTP_REQUEST_METHOD_PUT,
            HttpConstants.HTTP_REQUEST_METHOD_DELETE
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods(ORIGINS)
                .maxAge(3600);
    }


}
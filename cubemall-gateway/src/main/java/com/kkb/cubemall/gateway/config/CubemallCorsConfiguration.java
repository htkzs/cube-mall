package com.kkb.cubemall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//造成跨域的原因在于 原访问地址 http://localhost:8888/api 而需要路由的地址 http://localhost:8080/renren-fast
@Configuration
public class CubemallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); //可以携带cookie信息
        config.addAllowedOrigin("*"); //允许所有的请求地址 访问
        config.addAllowedHeader("*"); //允许携带所有的请求头信息
        config.addAllowedMethod("*"); //允许所有的请求方式 get post put delete option

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //配置所有的访问地址都生效
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}

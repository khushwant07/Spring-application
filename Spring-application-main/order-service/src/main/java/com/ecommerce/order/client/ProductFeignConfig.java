package com.ecommerce.order.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ProductFeignConfig {

    @Bean
    public RequestInterceptor serviceTokenInterceptor(@Value("${service.internal-token}") String token) {
        return template -> template.header("X-Service-Token", token);
    }
}

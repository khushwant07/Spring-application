package com.ecommerce.gateway.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayWebFiltersConfig {

    @Bean
    public CorsWebFilter corsWebFilter(@Value("${gateway.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(config::addAllowedOriginPattern);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.setAllowedMethods(
                Arrays.asList(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    public GlobalFilter securityHeadersFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
            exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
            exchange.getResponse().getHeaders().add("Referrer-Policy", "no-referrer");
            return chain.filter(exchange);
        };
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 10)
    public GlobalFilter requestLoggingFilter() {
        return (exchange, chain) -> {
            long start = System.currentTimeMillis();
            return chain.filter(exchange)
                    .then(
                            Mono.fromRunnable(
                                    () -> {
                                        long ms = System.currentTimeMillis() - start;
                                        var req = exchange.getRequest();
                                        org.slf4j.LoggerFactory.getLogger("gateway.access")
                                                .info(
                                                        "{} {} -> {} ({} ms)",
                                                        req.getMethod(),
                                                        req.getURI().getPath(),
                                                        exchange.getResponse().getStatusCode(),
                                                        ms);
                                    }));
        };
    }
}

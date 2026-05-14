package com.ecommerce.gateway.config;

import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RedisRateLimiter gatewayAuthLimiter() {
        return new RedisRateLimiter(4, 8);
    }

    @Bean
    @Primary
    public RedisRateLimiter gatewayPublicReadLimiter() {
        return new RedisRateLimiter(60, 120);
    }

    @Bean
    public RedisRateLimiter gatewayWriteLimiter() {
        return new RedisRateLimiter(25, 50);
    }

    @Bean
    public RedisRateLimiter gatewayOrderLimiter() {
        return new RedisRateLimiter(30, 60);
    }

    @Bean
    public KeyResolver gatewayKeyResolver(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${gateway.bypass.header-name}") String bypassHeader,
            @Value("${gateway.bypass.secret}") String bypassSecret) {
        byte[] bytes = jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes");
        }
        SecretKey key = new SecretKeySpec(bytes, "HmacSHA256");
        return exchange ->
                Mono.fromCallable(() -> resolveKey(exchange, key, bypassHeader, bypassSecret))
                        .subscribeOn(Schedulers.boundedElastic());
    }

    private static String resolveKey(
            ServerWebExchange exchange,
            SecretKey key,
            String bypassHeader,
            String bypassSecret) {
        String bypassValue = exchange.getRequest().getHeaders().getFirst(bypassHeader);
        if (bypassSecret.equals(bypassValue)) {
            return "internal-bypass";
        }
        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                String token = auth.substring(7);
                var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
                return "user:" + claims.getSubject();
            } catch (Exception ignored) {
                return "ip:" + clientIp(exchange);
            }
        }
        return "ip:" + clientIp(exchange);
    }

    private static String clientIp(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        var remote = exchange.getRequest().getRemoteAddress();
        return remote == null || remote.getAddress() == null ? "unknown" : remote.getAddress().getHostAddress();
    }

    @Bean
    public RouteLocator gatewayRoutes(
            RouteLocatorBuilder builder,
            @Value("${gateway.bypass.header-name}") String bypassHeader,
            @Value("${gateway.bypass.secret}") String bypassSecret,
            RedisRateLimiter gatewayAuthLimiter,
            RedisRateLimiter gatewayPublicReadLimiter,
            RedisRateLimiter gatewayWriteLimiter,
            RedisRateLimiter gatewayOrderLimiter,
            KeyResolver gatewayKeyResolver) {
        String quoted = java.util.regex.Pattern.quote(bypassSecret);
        return builder.routes()
                .route(
                        "bypass-auth",
                        r ->
                                r.order(0)
                                        .path("/api/auth/**")
                                        .and()
                                        .header(bypassHeader, quoted)
                                        .uri("lb://auth-service"))
                .route(
                        "bypass-products",
                        r ->
                                r.order(0)
                                        .path("/api/products/**")
                                        .and()
                                        .header(bypassHeader, quoted)
                                        .uri("lb://product-service"))
                .route(
                        "bypass-categories",
                        r ->
                                r.order(0)
                                        .path("/api/categories/**")
                                        .and()
                                        .header(bypassHeader, quoted)
                                        .uri("lb://product-service"))
                .route(
                        "bypass-orders",
                        r ->
                                r.order(0)
                                        .path("/api/orders/**")
                                        .and()
                                        .header(bypassHeader, quoted)
                                        .uri("lb://order-service"))
                .route(
                        "auth-rate-limited",
                        r ->
                                r.order(20)
                                        .path("/api/auth/**")
                                        .filters(
                                                f ->
                                                        f.requestRateLimiter(
                                                                c -> {
                                                                    c.setRateLimiter(gatewayAuthLimiter);
                                                                    c.setKeyResolver(gatewayKeyResolver);
                                                                    c.setDenyEmptyKey(false);
                                                                }))
                                        .uri("lb://auth-service"))
                .route(
                        "products-get-rate-limited",
                        r ->
                                r.order(21)
                                        .path("/api/products/**")
                                        .and()
                                        .predicate(
                                                exchange ->
                                                        exchange.getRequest().getMethod() == HttpMethod.GET)
                                        .filters(
                                                f ->
                                                        f.requestRateLimiter(
                                                                c -> {
                                                                    c.setRateLimiter(gatewayPublicReadLimiter);
                                                                    c.setKeyResolver(gatewayKeyResolver);
                                                                    c.setDenyEmptyKey(false);
                                                                }))
                                        .uri("lb://product-service"))
                .route(
                        "categories-get-rate-limited",
                        r ->
                                r.order(21)
                                        .path("/api/categories/**")
                                        .and()
                                        .predicate(
                                                exchange ->
                                                        exchange.getRequest().getMethod() == HttpMethod.GET)
                                        .filters(
                                                f ->
                                                        f.requestRateLimiter(
                                                                c -> {
                                                                    c.setRateLimiter(gatewayPublicReadLimiter);
                                                                    c.setKeyResolver(gatewayKeyResolver);
                                                                    c.setDenyEmptyKey(false);
                                                                }))
                                        .uri("lb://product-service"))
                .route(
                        "products-mutation-rate-limited",
                        r ->
                                r.order(22)
                                        .path("/api/products/**")
                                        .and()
                                        .predicate(
                                                exchange -> {
                                                    var m = exchange.getRequest().getMethod();
                                                    return m == HttpMethod.POST
                                                            || m == HttpMethod.PUT
                                                            || m == HttpMethod.PATCH
                                                            || m == HttpMethod.DELETE;
                                                })
                                        .filters(
                                                f ->
                                                        f.requestRateLimiter(
                                                                c -> {
                                                                    c.setRateLimiter(gatewayWriteLimiter);
                                                                    c.setKeyResolver(gatewayKeyResolver);
                                                                    c.setDenyEmptyKey(false);
                                                                }))
                                        .uri("lb://product-service"))
                .route(
                        "categories-mutation-rate-limited",
                        r ->
                                r.order(22)
                                        .path("/api/categories/**")
                                        .and()
                                        .predicate(
                                                exchange -> {
                                                    var m = exchange.getRequest().getMethod();
                                                    return m == HttpMethod.POST
                                                            || m == HttpMethod.PUT
                                                            || m == HttpMethod.PATCH
                                                            || m == HttpMethod.DELETE;
                                                })
                                        .filters(
                                                f ->
                                                        f.requestRateLimiter(
                                                                c -> {
                                                                    c.setRateLimiter(gatewayWriteLimiter);
                                                                    c.setKeyResolver(gatewayKeyResolver);
                                                                    c.setDenyEmptyKey(false);
                                                                }))
                                        .uri("lb://product-service"))
                .route(
                        "orders-rate-limited",
                        r ->
                                r.order(20)
                                        .path("/api/orders/**")
                                        .filters(
                                                f ->
                                                        f.requestRateLimiter(
                                                                c -> {
                                                                    c.setRateLimiter(gatewayOrderLimiter);
                                                                    c.setKeyResolver(gatewayKeyResolver);
                                                                    c.setDenyEmptyKey(false);
                                                                }))
                                        .uri("lb://order-service"))
                .build();
    }
}

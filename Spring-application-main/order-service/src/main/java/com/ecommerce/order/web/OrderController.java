package com.ecommerce.order.web;

import com.ecommerce.order.service.OrderService;
import com.ecommerce.order.web.dto.CheckoutRequest;
import com.ecommerce.order.web.dto.OrderResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal JwtAuthenticationToken auth,
            @Valid @RequestBody CheckoutRequest request) {
        Long userId = Long.parseLong(auth.getToken().getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(userId, request));
    }

    @GetMapping
    public List<OrderResponse> list(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        Long userId = Long.parseLong(auth.getToken().getSubject());
        boolean admin =
                auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return orderService.list(userId, admin);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@AuthenticationPrincipal JwtAuthenticationToken auth, @PathVariable Long id) {
        Long userId = Long.parseLong(auth.getToken().getSubject());
        boolean admin =
                auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return orderService.get(id, userId, admin);
    }
}

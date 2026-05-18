package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductInternalClient;
import com.ecommerce.order.client.dto.InventoryCommitLine;
import com.ecommerce.order.client.dto.InventoryCommitRequest;
import com.ecommerce.order.domain.CustomerOrder;
import com.ecommerce.order.domain.OrderItem;
import com.ecommerce.order.repo.OrderRepository;
import com.ecommerce.order.web.dto.CheckoutRequest;
import com.ecommerce.order.web.dto.OrderResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductInternalClient productClient;

    public OrderService(OrderRepository orderRepository, ProductInternalClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        List<InventoryCommitLine> lines =
                request.lines().stream()
                        .map(l -> new InventoryCommitLine(l.productId(), l.quantity()))
                        .toList();
        var commit =
                productClient.commit(new InventoryCommitRequest(lines));
        BigDecimal total = BigDecimal.ZERO;
        for (var line : commit.lines()) {
            total =
                    total.add(
                            line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }
        CustomerOrder order = new CustomerOrder();
        order.setUserId(userId);
        order.setTotalAmount(total);
        for (var line : commit.lines()) {
            OrderItem item = new OrderItem();
            item.setProductId(line.productId());
            item.setProductName(line.productName());
            item.setUnitPrice(line.unitPrice());
            item.setQuantity(line.quantity());
            order.addItem(item);
        }
        orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list(Long userId, boolean admin) {
        if (admin) {
            return orderRepository
                    .findAllByOrderByCreatedAtDesc(PageRequest.of(0, 100))
                    .map(this::toResponse)
                    .toList();
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long orderId, Long userId, boolean admin) {
        CustomerOrder order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!admin && !order.getUserId().equals(userId)) {
            throw new AccessDeniedException("Forbidden");
        }
        return toResponse(order);
    }

    private OrderResponse toResponse(CustomerOrder order) {
        var lines =
                order.getItems().stream()
                        .map(
                                i ->
                                        new OrderResponse.OrderLineResponse(
                                                i.getProductId(),
                                                i.getProductName(),
                                                i.getUnitPrice(),
                                                i.getQuantity()))
                        .toList();
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                lines);
    }
}

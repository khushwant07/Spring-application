package com.ecommerce.order.repo;

import com.ecommerce.order.domain.CustomerOrder;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<CustomerOrder> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

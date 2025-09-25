package com.codingday.plushspring.repository;

import com.codingday.plushspring.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndOrderDateAfter(Long customerId, LocalDateTime after);

}

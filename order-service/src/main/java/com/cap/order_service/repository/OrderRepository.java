package com.cap.order_service.repository;

import com.cap.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get orders of a user
    List<Order> findByUserEmail(String userEmail);
}

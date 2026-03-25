package com.cap.admin_service.feign;

import com.cap.admin_service.config.FeignConfig;
import com.cap.admin_service.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ORDER-SERVICE", configuration = FeignConfig.class)
public interface OrderClient {

    // 🔹 GET ALL ORDERS
    @GetMapping("/api/orders")
    List<OrderResponse> getAllOrders();

    // 🔹 UPDATE ORDER STATUS
    @PutMapping("/api/orders/{id}/status")
    String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    );
}
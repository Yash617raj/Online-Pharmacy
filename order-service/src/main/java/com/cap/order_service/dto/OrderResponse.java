package com.cap.order_service.dto;

import com.cap.order_service.entity.OrderStatus;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private List<OrderItemDTO> items;
    private Double totalAmount;
    private OrderStatus status;
    private String address;
}
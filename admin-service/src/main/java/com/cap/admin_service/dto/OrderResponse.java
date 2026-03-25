package com.cap.admin_service.dto;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private List<OrderItemDTO> items;
    private Double totalAmount;
    private String status;
    private String address;
}
package com.cap.order_service.dto;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    private Long medicineId;
    private String medicineName;
    private Double price;
    private Integer quantity;
}
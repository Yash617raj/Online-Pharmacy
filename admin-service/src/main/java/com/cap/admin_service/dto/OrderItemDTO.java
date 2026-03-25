package com.cap.admin_service.dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {

    private Long medicineId;

    private String medicineName;
    private Double price;
    private Integer quantity;
}
package com.cap.order_service.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotNull
    private Long medicineId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
package com.cap.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotBlank
    private String address;
}
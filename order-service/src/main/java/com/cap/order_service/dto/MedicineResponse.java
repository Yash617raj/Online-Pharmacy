package com.cap.order_service.dto;

import lombok.Data;

@Data
public class MedicineResponse {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private boolean prescriptionRequired;
}
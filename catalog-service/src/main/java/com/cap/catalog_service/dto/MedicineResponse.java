package com.cap.catalog_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class MedicineResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private boolean prescriptionRequired;
    private String categoryName;
}
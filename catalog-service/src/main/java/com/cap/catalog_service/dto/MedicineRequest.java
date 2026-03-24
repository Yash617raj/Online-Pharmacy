package com.cap.catalog_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MedicineRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Min(0)
    private Integer stock;

    private boolean prescriptionRequired;

    @NotNull
    private Long categoryId;
}
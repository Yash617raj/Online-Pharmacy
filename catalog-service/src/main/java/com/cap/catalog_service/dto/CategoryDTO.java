package com.cap.catalog_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {

    @NotBlank
    private String name;
}
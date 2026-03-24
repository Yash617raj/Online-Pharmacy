package com.cap.catalog_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PrescriptionUploadRequest {

    @NotNull
    private MultipartFile file;

    @NotNull
    private Long medicineId;
}
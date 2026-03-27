package com.cap.admin_service.feign;
import com.cap.admin_service.config.FeignConfig;
import com.cap.admin_service.dto.PrescriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "CATALOG-SERVICE", configuration = FeignConfig.class)
public interface CatalogClient {

    @GetMapping("/api/catalog/prescriptions")
    List<PrescriptionDTO> getAllPrescriptions();

    @PutMapping("/api/catalog/prescriptions/{id}/status")
    String updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestParam String status
    );
}

package com.cap.order_service.feign;

import com.cap.order_service.config.FeignConfig;
import com.cap.order_service.dto.MedicineResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CATALOG-SERVICE", configuration = FeignConfig.class)
public interface CatalogClient {

    @GetMapping("/api/catalog/medicines/{id}")
    MedicineResponse getMedicine(@PathVariable Long id);
}

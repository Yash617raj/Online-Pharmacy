package com.cap.admin_service.service;

import com.cap.admin_service.dto.OrderResponse;
import com.cap.admin_service.dto.PrescriptionDTO;
import com.cap.admin_service.entity.AdminActionLog;
import com.cap.admin_service.exception.ApiException;
import com.cap.admin_service.feign.CatalogClient;
import com.cap.admin_service.feign.OrderClient;
import com.cap.admin_service.repository.AdminActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CatalogClient catalogClient;
    private final OrderClient orderClient;
    private final AdminActionLogRepository logRepository;

    public List<PrescriptionDTO> getAllPrescriptions() {
        return catalogClient.getAllPrescriptions();
    }

    public String updatePrescription(Long id, String status, String adminEmail) {

        if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
            throw new ApiException("Invalid prescription status");
        }

        String response = catalogClient.updatePrescriptionStatus(id, status);

        logRepository.save(
                AdminActionLog.builder()
                        .adminEmail(adminEmail)
                        .action("UPDATE_PRESCRIPTION")
                        .targetType("PRESCRIPTION")
                        .targetId(id)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return response;
    }

    public List<OrderResponse> getAllOrders() {
        return orderClient.getAllOrders();
    }

    public String updateOrderStatus(Long orderId, String status, String adminEmail) {

        if (status == null || status.isBlank()) {
            throw new ApiException("Status cannot be empty");
        }

        String response = orderClient.updateOrderStatus(orderId, status);

        logRepository.save(
                AdminActionLog.builder()
                        .adminEmail(adminEmail)
                        .action("UPDATE_ORDER")
                        .targetType("ORDER")
                        .targetId(orderId)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        return response;
    }
}
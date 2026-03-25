package com.cap.admin_service.controller;

import com.cap.admin_service.dto.OrderResponse;
import com.cap.admin_service.dto.PrescriptionDTO;
import com.cap.admin_service.exception.ApiException;
import com.cap.admin_service.service.AdminService;
import com.cap.admin_service.util.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;
    private final JwtUtil jwtUtil;

    // 🔐 COMMON ADMIN CHECK
    private String validateAdmin(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equals(role)) {
            throw new ApiException("Access denied. Admin only.");
        }

        return jwtUtil.extractUsername(token);
    }

    // 🔹 GET ALL PRESCRIPTIONS
    @GetMapping("/prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptions(
            @RequestHeader("Authorization") String authHeader) {

        validateAdmin(authHeader);

        return ResponseEntity.ok(service.getAllPrescriptions());
    }

    // 🔹 APPROVE / REJECT PRESCRIPTION
    @PutMapping("/prescriptions/{id}")
    public ResponseEntity<?> updatePrescription(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("Authorization") String authHeader) {

        String adminEmail = validateAdmin(authHeader);

        return ResponseEntity.ok(
                service.updatePrescription(id, status, adminEmail)
        );
    }

    // 🔹 GET ALL ORDERS
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestHeader("Authorization") String authHeader) {

        validateAdmin(authHeader);

        return ResponseEntity.ok(service.getAllOrders());
    }

    // 🔹 UPDATE ORDER STATUS
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam @NotBlank String status,
            @RequestHeader("Authorization") String authHeader) {

        String adminEmail = validateAdmin(authHeader);

        return ResponseEntity.ok(
                service.updateOrderStatus(id, status, adminEmail)
        );
    }
}
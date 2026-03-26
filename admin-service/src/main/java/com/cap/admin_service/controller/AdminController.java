package com.cap.admin_service.controller;

import com.cap.admin_service.dto.OrderResponse;
import com.cap.admin_service.dto.PrescriptionDTO;
import com.cap.admin_service.service.AdminService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    // GET ALL PRESCRIPTIONS
    @GetMapping("/prescriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptions(
            @AuthenticationPrincipal String adminEmail) {

        return ResponseEntity.ok(service.getAllPrescriptions());
    }

    // APPROVE / REJECT PRESCRIPTION
    @PutMapping("/prescriptions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePrescription(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal String adminEmail) {

        return ResponseEntity.ok(service.updatePrescription(id, status, adminEmail));
    }

    // GET ALL ORDERS
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrders() {

        return ResponseEntity.ok(service.getAllOrders());
    }

    // UPDATE ORDER STATUS
    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam @NotBlank String status,
            @AuthenticationPrincipal String adminEmail) {

        return ResponseEntity.ok(service.updateOrderStatus(id, status, adminEmail));
    }
}
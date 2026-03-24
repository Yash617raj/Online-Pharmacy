package com.cap.catalog_service.controller;
import com.cap.catalog_service.dto.*;
import com.cap.catalog_service.exception.ApiException;
import com.cap.catalog_service.service.CatalogService;
import com.cap.catalog_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final JwtUtil jwtUtil;
    private final CatalogService service;

    // 🔹 GET ALL MEDICINES
    @GetMapping("/medicines")
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {
        return ResponseEntity.ok(service.getAllMedicines());
    }

    // 🔹 GET MEDICINE BY ID
    @GetMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponse> getMedicine(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getMedicine(id));
    }

    // 🔹 SEARCH MEDICINES
    @GetMapping("/medicines/search")
    public ResponseEntity<List<MedicineResponse>> searchMedicines(
            @RequestParam String keyword) {

        return ResponseEntity.ok(service.searchMedicines(keyword));
    }

    // 🔹 CREATE MEDICINE (ADMIN use — later secured)
    @PostMapping("/medicines")
    public ResponseEntity<?> createMedicine(
            @Valid @RequestBody MedicineRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        String role = jwtUtil.extractRole(token);

        // 🔥 ADMIN CHECK
        if (!"ADMIN".equals(role)) {
            throw new ApiException("Access denied. Admin only.");
        }

        service.createMedicine(request);

        return ResponseEntity.ok("Medicine created");
    }

    // 🔹 UPLOAD PRESCRIPTION
    @PostMapping(value = "/prescriptions/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadPrescription(
            @ModelAttribute PrescriptionUploadRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        String userEmail = jwtUtil.extractUsername(token);

        service.uploadPrescription(request, userEmail);

        return ResponseEntity.ok("Prescription uploaded");
    }
}
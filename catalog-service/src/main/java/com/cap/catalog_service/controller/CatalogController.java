package com.cap.catalog_service.controller;

import com.cap.catalog_service.dto.*;
import com.cap.catalog_service.entity.Prescription;
import com.cap.catalog_service.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService service;

    @GetMapping("/medicines")
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {
        return ResponseEntity.ok(service.getAllMedicines());
    }

    @GetMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponse> getMedicine(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMedicine(id));
    }

    @GetMapping("/medicines/search")
    public ResponseEntity<List<MedicineResponse>> searchMedicines(
            @RequestParam String keyword) {
        return ResponseEntity.ok(service.searchMedicines(keyword));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO dto) {
        service.createCategory(dto);
        return ResponseEntity.ok("Category created");
    }

    @GetMapping("/prescriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(service.getAllPrescriptions());
    }

    @PostMapping("/medicines")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMedicine(
            @Valid @RequestBody MedicineRequest request) {

        service.createMedicine(request);
        return ResponseEntity.ok("Medicine created");
    }

    @PutMapping("/prescriptions/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        service.updatePrescriptionStatus(id, status);
        return ResponseEntity.ok("Prescription updated");
    }

    @PostMapping(value = "/prescriptions/upload", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadPrescription(
            @ModelAttribute PrescriptionUploadRequest request,
            @AuthenticationPrincipal String userEmail) {

        service.uploadPrescription(request, userEmail);
        return ResponseEntity.ok("Prescription uploaded");
    }
}
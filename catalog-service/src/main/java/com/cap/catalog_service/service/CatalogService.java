package com.cap.catalog_service.service;

import com.cap.catalog_service.dto.*;
import com.cap.catalog_service.entity.*;
import com.cap.catalog_service.exception.ApiException;
import com.cap.catalog_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final MedicineRepository medicineRepository;
    private final CategoryRepository categoryRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ModelMapper mapper;

    // 🔹 GET ALL MEDICINES
    public List<MedicineResponse> getAllMedicines() {

        return medicineRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🔹 GET MEDICINE BY ID
    public MedicineResponse getMedicine(Long id) {

        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ApiException("Medicine not found"));

        return mapToResponse(medicine);
    }

    // 🔹 SEARCH MEDICINE
    public List<MedicineResponse> searchMedicines(String keyword) {

        return medicineRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🔹 CREATE MEDICINE (Admin)
    public void createMedicine(MedicineRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found"));

        Medicine medicine = mapper.map(request, Medicine.class);
        medicine.setCategory(category);

        medicineRepository.save(medicine);
    }

    // 🔹 UPLOAD PRESCRIPTION
    public void uploadPrescription(PrescriptionUploadRequest request,
                                   String userEmail) {

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new ApiException("Medicine not found"));

        MultipartFile file = request.getFile();

        // 🔥 Basic validation
        if (file.isEmpty()) {
            throw new ApiException("File is empty");
        }

        // ⚠️ For now (simple version)
        String fileUrl = "uploads/" + file.getOriginalFilename();

        Prescription prescription = Prescription.builder()
                .fileUrl(fileUrl)
                .status("PENDING")
                .userEmail(userEmail)
                .medicine(medicine)
                .build();

        prescriptionRepository.save(prescription);
    }

    // 🔹 Helper method
    private MedicineResponse mapToResponse(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .description(medicine.getDescription())
                .price(medicine.getPrice())
                .stock(medicine.getStock())
                .prescriptionRequired(medicine.isPrescriptionRequired())
                .categoryName(medicine.getCategory().getName())
                .build();
    }
}
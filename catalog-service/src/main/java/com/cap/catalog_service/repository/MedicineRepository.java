package com.cap.catalog_service.repository;

import com.cap.catalog_service.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // 🔍 Search by name (PRD requirement)
    List<Medicine> findByNameContainingIgnoreCase(String name);

    // 🔍 Filter by category
    List<Medicine> findByCategoryId(Long categoryId);
}
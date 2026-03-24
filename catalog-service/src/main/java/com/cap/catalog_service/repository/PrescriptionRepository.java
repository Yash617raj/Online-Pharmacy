package com.cap.catalog_service.repository;

import com.cap.catalog_service.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByUserEmail(String userEmail);

    List<Prescription> findByStatus(String status);
}

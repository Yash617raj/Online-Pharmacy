package com.cap.catalog_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl;

    private String status; // PENDING, APPROVED, REJECTED

    private String userEmail;

    @ManyToOne
    private Medicine medicine;
}
package com.cap.admin_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adminEmail;

    private String action; // APPROVED_PRESCRIPTION, UPDATED_ORDER

    private String targetType; // PRESCRIPTION, ORDER

    private Long targetId;

    private LocalDateTime timestamp;
}
package com.cap.admin_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long totalOrders;
    private Long pendingPrescriptions;
    private Long lowStockItems;
    private Double totalRevenue;
}
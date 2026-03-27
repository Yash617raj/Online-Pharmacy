package com.cap.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long medicineId;

    private String medicineName;

    private Double price;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
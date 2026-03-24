package com.cap.catalog_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private boolean prescriptionRequired;

    // 🔗 Relation with Category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

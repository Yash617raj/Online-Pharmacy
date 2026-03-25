package com.cap.admin_service.dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionDTO {

    private Long id;
    private String fileUrl;
    private String status;
    private String userEmail;
    private Long medicineId;
}
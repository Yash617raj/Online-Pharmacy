package com.cap.admin_service.repository;
import com.cap.admin_service.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

    // 🔍 Get logs by admin
    List<AdminActionLog> findByAdminEmail(String adminEmail);

    // 🔍 Get logs by action type
    List<AdminActionLog> findByAction(String action);
}
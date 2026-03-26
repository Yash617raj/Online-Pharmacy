package com.cap.admin_service.repository;
import com.cap.admin_service.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

    List<AdminActionLog> findByAdminEmail(String adminEmail);

    List<AdminActionLog> findByAction(String action);
}
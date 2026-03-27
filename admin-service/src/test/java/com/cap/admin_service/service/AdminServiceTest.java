package com.cap.admin_service.service;

import com.cap.admin_service.dto.OrderResponse;
import com.cap.admin_service.dto.PrescriptionDTO;
import com.cap.admin_service.entity.AdminActionLog;
import com.cap.admin_service.exception.ApiException;
import com.cap.admin_service.feign.CatalogClient;
import com.cap.admin_service.feign.OrderClient;
import com.cap.admin_service.repository.AdminActionLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private OrderClient orderClient;

    @Mock
    private AdminActionLogRepository logRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getAllPrescriptions_success() {
        List<PrescriptionDTO> mockList = List.of(new PrescriptionDTO());

        when(catalogClient.getAllPrescriptions()).thenReturn(mockList);

        List<PrescriptionDTO> result = adminService.getAllPrescriptions();

        assertEquals(1, result.size());
        verify(catalogClient).getAllPrescriptions();
    }

    @Test
    void getAllPrescriptions_emptyList() {
        when(catalogClient.getAllPrescriptions()).thenReturn(List.of());

        List<PrescriptionDTO> result = adminService.getAllPrescriptions();

        assertTrue(result.isEmpty());
    }

    @Test
    void updatePrescription_success_APPROVED() {
        when(catalogClient.updatePrescriptionStatus(1L, "APPROVED"))
                .thenReturn("Updated");

        String result = adminService.updatePrescription(1L, "APPROVED", "admin@test.com");

        assertEquals("Updated", result);
        verify(logRepository).save(any(AdminActionLog.class));
    }

    @Test
    void updatePrescription_success_REJECTED() {
        when(catalogClient.updatePrescriptionStatus(1L, "REJECTED"))
                .thenReturn("Rejected");

        String result = adminService.updatePrescription(1L, "REJECTED", "admin@test.com");

        assertEquals("Rejected", result);
    }

    @Test
    void updatePrescription_invalidStatus() {
        assertThrows(ApiException.class, () ->
                adminService.updatePrescription(1L, "PENDING", "admin@test.com")
        );
    }

    @Test
    void updatePrescription_nullStatus() {
        assertThrows(NullPointerException.class, () ->
                adminService.updatePrescription(1L, null, "admin@test.com")
        );
    }

    @Test
    void updatePrescription_catalogFailure() {
        when(catalogClient.updatePrescriptionStatus(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Service down"));

        assertThrows(RuntimeException.class, () ->
                adminService.updatePrescription(1L, "APPROVED", "admin@test.com")
        );
    }

    @Test
    void getAllOrders_success() {
        List<OrderResponse> orders = List.of(new OrderResponse());

        when(orderClient.getAllOrders()).thenReturn(orders);

        List<OrderResponse> result = adminService.getAllOrders();

        assertEquals(1, result.size());
        verify(orderClient).getAllOrders();
    }

    @Test
    void getAllOrders_empty() {
        when(orderClient.getAllOrders()).thenReturn(List.of());

        List<OrderResponse> result = adminService.getAllOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    void updateOrderStatus_success() {
        when(orderClient.updateOrderStatus(1L, "DELIVERED"))
                .thenReturn("Updated");

        String result = adminService.updateOrderStatus(1L, "DELIVERED", "admin@test.com");

        assertEquals("Updated", result);
        verify(logRepository).save(any(AdminActionLog.class));
    }

    @Test
    void updateOrderStatus_blankStatus() {
        assertThrows(ApiException.class, () ->
                adminService.updateOrderStatus(1L, " ", "admin@test.com")
        );
    }

    @Test
    void updateOrderStatus_nullStatus() {
        assertThrows(ApiException.class, () ->
                adminService.updateOrderStatus(1L, null, "admin@test.com")
        );
    }

    @Test
    void updateOrderStatus_orderClientFailure() {
        when(orderClient.updateOrderStatus(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Order service down"));

        assertThrows(RuntimeException.class, () ->
                adminService.updateOrderStatus(1L, "DELIVERED", "admin@test.com")
        );
    }

    @Test
    void updateOrderStatus_verifyLogging() {
        when(orderClient.updateOrderStatus(1L, "SHIPPED"))
                .thenReturn("Done");

        adminService.updateOrderStatus(1L, "SHIPPED", "admin@test.com");

        verify(logRepository, times(1)).save(any(AdminActionLog.class));
    }

    @Test
    void updateOrderStatus_verifyNoLoggingOnFailure() {
        when(orderClient.updateOrderStatus(anyLong(), anyString()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () ->
                adminService.updateOrderStatus(1L, "SHIPPED", "admin@test.com")
        );

        verify(logRepository, never()).save(any());
    }
}
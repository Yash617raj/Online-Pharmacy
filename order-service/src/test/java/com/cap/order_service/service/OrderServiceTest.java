package com.cap.order_service.service;

import com.cap.order_service.dto.*;
import com.cap.order_service.entity.*;
import com.cap.order_service.exception.ApiException;
import com.cap.order_service.feign.CatalogClient;
import com.cap.order_service.repository.*;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private CatalogClient catalogClient;

    @InjectMocks private OrderService service;

    private MedicineResponse medicine() {
        MedicineResponse m = new MedicineResponse();
        m.setId(1L);
        m.setName("Paracetamol");
        m.setPrice(10.0);
        m.setStock(10);
        m.setPrescriptionRequired(false);
        return m;
    }

    private CartItemRequest cartItem() {
        CartItemRequest c = new CartItemRequest();
        c.setMedicineId(1L);
        c.setQuantity(2);
        return c;
    }

    private Order orderWithItems() {
        OrderItem item = new OrderItem();
        item.setMedicineId(1L);
        item.setMedicineName("Paracetamol");
        item.setPrice(10.0);
        item.setQuantity(2);

        Order order = new Order();
        order.setItems(new ArrayList<>(List.of(item)));
        order.setStatus(OrderStatus.CART);
        return order;
    }

    @Test
    void createCart_Success() {
        when(catalogClient.getMedicine(1L)).thenReturn(medicine());
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order order = service.createCart("user@gmail.com", List.of(cartItem()));

        assertEquals(20.0, order.getTotalAmount());
        assertEquals(OrderStatus.CART, order.getStatus());
    }

    @Test
    void createCart_MedicineNotFound() {
        when(catalogClient.getMedicine(1L)).thenThrow(FeignException.class);

        assertThrows(ApiException.class,
                () -> service.createCart("user", List.of(cartItem())));
    }

    @Test
    void createCart_InsufficientStock() {
        MedicineResponse m = medicine();
        m.setStock(1);

        when(catalogClient.getMedicine(1L)).thenReturn(m);

        assertThrows(ApiException.class,
                () -> service.createCart("user", List.of(cartItem())));
    }

    @Test
    void createCart_PrescriptionRequired() {
        MedicineResponse m = medicine();
        m.setPrescriptionRequired(true);

        when(catalogClient.getMedicine(1L)).thenReturn(m);

        assertThrows(ApiException.class,
                () -> service.createCart("user", List.of(cartItem())));
    }

    @Test
    void createCart_SaveCalled() {
        when(catalogClient.getMedicine(1L)).thenReturn(medicine());
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.createCart("user", List.of(cartItem()));

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_Success() {
        Order order = orderWithItems();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        service.updateOrderStatus(1L, "PAID");

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void updateOrderStatus_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.updateOrderStatus(1L, "PAID"));
    }

    @Test
    void updateOrderStatus_InvalidEnum() {
        Order order = orderWithItems();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateOrderStatus(1L, "INVALID"));
    }

    @Test
    void startCheckout_Success() {
        Order order = orderWithItems();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        CheckoutRequest req = new CheckoutRequest();
        req.setAddress("Delhi");

        OrderResponse res = service.startCheckout(1L, req);

        assertEquals(OrderStatus.CHECKOUT_STARTED, order.getStatus());
        assertNotNull(res);
    }

    @Test
    void startCheckout_InvalidState() {
        Order order = orderWithItems();
        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ApiException.class,
                () -> service.startCheckout(1L, new CheckoutRequest()));
    }

    @Test
    void startCheckout_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.startCheckout(1L, new CheckoutRequest()));
    }

    @Test
    void initiatePayment_Success() {
        Order order = orderWithItems();
        order.setStatus(OrderStatus.CHECKOUT_STARTED);
        order.setTotalAmount(100.0);

        PaymentRequest req = new PaymentRequest();
        req.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        String res = service.initiatePayment(req);

        assertEquals("Payment successful", res);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void initiatePayment_InvalidState() {
        Order order = orderWithItems();

        PaymentRequest req = new PaymentRequest();
        req.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ApiException.class,
                () -> service.initiatePayment(req));
    }

    @Test
    void initiatePayment_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentRequest req = new PaymentRequest();
        req.setOrderId(1L);

        assertThrows(ApiException.class,
                () -> service.initiatePayment(req));
    }

    @Test
    void getOrder_Success() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(orderWithItems()));

        OrderResponse res = service.getOrder(1L);

        assertNotNull(res);
    }

    @Test
    void getOrder_NotFound() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.getOrder(1L));
    }

    @Test
    void getUserOrders_Success() {
        when(orderRepository.findByUserEmail("user"))
                .thenReturn(List.of(orderWithItems(), orderWithItems()));

        List<OrderResponse> res = service.getUserOrders("user");
        assertEquals(2, res.size());
    }
}
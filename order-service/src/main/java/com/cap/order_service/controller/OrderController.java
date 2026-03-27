package com.cap.order_service.controller;

import com.cap.order_service.dto.*;
import com.cap.order_service.entity.Order;
import com.cap.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping("/cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createCart(
            @RequestBody List<CartItemRequest> items,
            @AuthenticationPrincipal String email) {

        Order order = service.createCart(email, items);
        return ResponseEntity.ok(service.getOrder(order.getId()));
    }

    @PostMapping("/checkout/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> checkout(
            @RequestParam Long orderId,
            @Valid @RequestBody CheckoutRequest request) {

        return ResponseEntity.ok(service.startCheckout(orderId, request));
    }

    @PostMapping("/payments/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> payment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(service.initiatePayment(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrder(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        service.updateOrderStatus(id, status);
        return ResponseEntity.ok("Order status updated");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @AuthenticationPrincipal String email) {

        return ResponseEntity.ok(service.getUserOrders(email));
    }
}

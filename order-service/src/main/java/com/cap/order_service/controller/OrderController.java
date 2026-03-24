package com.cap.order_service.controller;

import com.cap.order_service.dto.*;
import com.cap.order_service.entity.Order;
import com.cap.order_service.service.OrderService;
import com.cap.order_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;
    private final JwtUtil jwtUtil;

    // 🔹 CREATE CART
    @PostMapping("/cart")
    public ResponseEntity<Order> createCart(
            @RequestBody List<CartItemRequest> items,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                service.createCart(email, items)
        );
    }

    // 🔹 START CHECKOUT
    @PostMapping("/checkout/start")
    public ResponseEntity<OrderResponse> checkout(
            @RequestParam Long orderId,
            @Valid @RequestBody CheckoutRequest request) {

        return ResponseEntity.ok(
                service.startCheckout(orderId, request)
        );
    }

    // 🔹 INITIATE PAYMENT
    @PostMapping("/payments/initiate")
    public ResponseEntity<?> payment(
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(
                service.initiatePayment(request)
        );
    }

    // 🔹 GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {

        return ResponseEntity.ok(
                service.getOrder(id)
        );
    }

    // 🔹 GET USER ORDERS
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(
                service.getUserOrders(email)
        );
    }
}
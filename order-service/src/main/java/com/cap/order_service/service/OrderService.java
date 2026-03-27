package com.cap.order_service.service;
import com.cap.order_service.dto.*;
import com.cap.order_service.entity.*;
import com.cap.order_service.exception.ApiException;
import com.cap.order_service.feign.CatalogClient;
import com.cap.order_service.repository.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CatalogClient catalogClient;

    public Order createCart(String userEmail, List<CartItemRequest> items) {

        List<OrderItem> orderItems = items.stream()
                .map(item -> {

                    MedicineResponse medicine;

                    try {
                        medicine = catalogClient.getMedicine(item.getMedicineId());
                    } catch (FeignException e) {
                        throw new ApiException("Medicine not found");
                    }

                    if (medicine.getStock() < item.getQuantity()) {
                        throw new ApiException("Insufficient stock for " + medicine.getName());
                    }

                    if (medicine.isPrescriptionRequired()) {
                        throw new ApiException(
                                "Prescription required for " + medicine.getName()
                        );
                    }

                    return OrderItem.builder()
                            .medicineId(medicine.getId())
                            .medicineName(medicine.getName())
                            .price(medicine.getPrice())
                            .quantity(item.getQuantity())
                            .build();
                })
                .toList();

        double total = orderItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .userEmail(userEmail)
                .items(orderItems)
                .totalAmount(total)
                .status(OrderStatus.CART)
                .build();

        orderItems.forEach(item -> item.setOrder(order));

        return orderRepository.save(order);
    }

    public void updateOrderStatus(Long id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status));

        orderRepository.save(order);
    }

    public OrderResponse startCheckout(Long orderId, CheckoutRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found"));

        if (order.getStatus() != OrderStatus.CART) {
            throw new ApiException("Invalid order state for checkout");
        }

        order.setStatus(OrderStatus.CHECKOUT_STARTED);
        order.setAddress(request.getAddress());

        orderRepository.save(order);

        return mapToResponse(order);
    }

    public String initiatePayment(PaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ApiException("Order not found"));

        if (order.getStatus() != OrderStatus.CHECKOUT_STARTED) {
            throw new ApiException("Order not ready for payment");
        }

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .status("SUCCESS")
                .build();

        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return "Payment successful";
    }

    public OrderResponse getOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order not found"));

        return mapToResponse(order);
    }

    public List<OrderResponse> getUserOrders(String userEmail) {

        return orderRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemDTO> items = order.getItems()
                .stream()
                .map(i -> OrderItemDTO.builder()
                        .medicineId(i.getMedicineId())
                        .medicineName(i.getMedicineName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .build())
                .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .address(order.getAddress())
                .build();
    }
}
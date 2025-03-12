package com.ordermanagement.service.order.impl;

import com.ordermanagement.domain.entity.*;
import com.ordermanagement.domain.repository.OrderRepository;
import com.ordermanagement.service.order.OrderService;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrderHistory(String customerId) {
        log.debug("Fetching order history for customer: {}", customerId);
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        log.debug("Found {} orders for customer: {}", orders.size(), customerId);
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        log.debug("Fetching order details for ID: {}", orderId);
        return orderRepository.findById(orderId)
            .orElseThrow(() -> {
                log.error("Order not found with ID: {}", orderId);
                return new OrderNotFoundException("Order not found with id: " + orderId);
            });
    }

    @Override
    @Transactional
    public void submitRefundRequest(Long orderItemId, String description, String evidenceImageUrl) {
        log.debug("Processing refund request for order item: {}", orderItemId);
        
        // Find the order containing the item
        Order order = orderRepository.findAll().stream()
            .filter(o -> o.getItems().stream()
                .anyMatch(item -> item.getId().equals(orderItemId)))
            .findFirst()
            .orElseThrow(() -> {
                log.error("Order item not found with ID: {}", orderItemId);
                return new OrderNotFoundException("Order item not found with id: " + orderItemId);
            });

        // Validate order status
        if (order.getStatus() != OrderStatus.DELIVERED) {
            log.error("Cannot request refund for order {} with status {}", order.getId(), order.getStatus());
            throw new IllegalStateException("Refund can only be requested for delivered orders");
        }

        // Find the specific item
        OrderItem orderItem = order.getItems().stream()
            .filter(item -> item.getId().equals(orderItemId))
            .findFirst()
            .orElseThrow(() -> new OrderNotFoundException("Order item not found"));

        // Check if refund already exists
        if (orderItem.getRefundRequest() != null) {
            log.error("Refund request already exists for order item: {}", orderItemId);
            throw new IllegalStateException("Refund request already exists for this item");
        }

        // Create and set up the refund request
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderItem(orderItem);
        refundRequest.setDescription(description);
        refundRequest.setEvidenceImageUrl(evidenceImageUrl);
        refundRequest.setRequestDate(LocalDateTime.now());
        refundRequest.setStatus(RefundStatus.PENDING);

        // Update order item with refund request
        orderItem.setRefundRequest(refundRequest);
        
        // Save changes
        orderRepository.save(order);
        log.info("Refund request created successfully for order item: {}", orderItemId);
    }
}

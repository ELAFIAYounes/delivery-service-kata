package com.ordermanagement.service.order.impl;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.domain.entity.OrderItem;
import com.ordermanagement.domain.entity.RefundRequest;
import com.ordermanagement.domain.entity.RefundStatus;
import com.ordermanagement.domain.repository.OrderItemRepository;
import com.ordermanagement.domain.repository.OrderRepository;
import com.ordermanagement.domain.repository.RefundRequestRepository;
import com.ordermanagement.service.order.OrderService;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import com.ordermanagement.service.order.exception.RefundRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RefundRequestRepository refundRequestRepository;

    @Override
    public List<Order> getCustomerOrderHistory(String customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    @Override
    public Order submitRefundRequest(Long orderItemId, String description, String evidenceImageUrl) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new OrderNotFoundException("Order item not found with ID: " + orderItemId));

        // Check if a refund request already exists for this order item
        if (orderItem.getRefundRequest() != null) {
            throw new RefundRequestException("A refund request already exists for order item: " + orderItemId);
        }

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderItem(orderItem);
        refundRequest.setDescription(description);
        refundRequest.setEvidenceImageUrl(evidenceImageUrl);
        refundRequest.setRequestDate(LocalDateTime.now());
        refundRequest.setStatus(RefundStatus.PENDING);

        refundRequestRepository.save(refundRequest);
        return orderItem.getOrder();
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}

package com.ordermanagement.infrastructure.kafka.messaging;

import com.ordermanagement.application.messaging.OrderEventPublisher;
import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.domain.entity.RefundRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishOrderCreated(Order order) {
        try {
            log.info("Publishing order created event for order ID: {}", order.getId());
            kafkaTemplate.send("orders", order.getId().toString(), order);
            log.info("Successfully published order created event for order ID: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish order created event for order ID: {}", order.getId(), e);
            throw new RuntimeException("Failed to publish order event", e);
        }
    }

    @Override
    public void publishRefundRequested(RefundRequest refundRequest) {
        try {
            Long orderId = refundRequest.getOrderItem().getOrder().getId();
            log.info("Publishing refund request event for order item ID: {}", refundRequest.getOrderItem().getId());
            kafkaTemplate.send("refund-requests", orderId.toString(), refundRequest);
            log.info("Successfully published refund request event for order item ID: {}", refundRequest.getOrderItem().getId());
        } catch (Exception e) {
            log.error("Failed to publish refund request event for order item ID: {}", refundRequest.getOrderItem().getId(), e);
            throw new RuntimeException("Failed to publish refund request event", e);
        }
    }
}

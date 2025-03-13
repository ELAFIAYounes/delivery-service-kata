package com.ordermanagement.rest.api.order;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.service.order.OrderService;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import com.ordermanagement.service.order.exception.RefundRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController implements OrderAPI {

    private final OrderService orderService;

    @Override
    public ResponseEntity<List<Order>> getCustomerOrderHistory(String customerId) {
        try {
            log.info("Fetching order history for customer: {}", customerId);
            List<Order> orders = orderService.getCustomerOrderHistory(customerId);
            log.info("Found {} orders for customer: {}", orders.size(), customerId);
            return ResponseEntity.ok(orders);
        } catch (OrderNotFoundException e) {
            log.error("Customer not found: {}", customerId);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> submitRefundRequest(Long orderItemId, RefundRequestDTO refundRequest) {
        try {
            log.info("Processing refund request for order item: {}", orderItemId);
            orderService.submitRefundRequest(
                orderItemId,
                refundRequest.getDescription(),
                refundRequest.getEvidenceImageUrl()
            );
            log.info("Refund request processed successfully for order item: {}", orderItemId);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException e) {
            log.error("Order item not found for refund request: {}", orderItemId);
            return ResponseEntity.notFound().build();
        } catch (RefundRequestException e) {
            log.error("Refund request error for order item {}: {}", orderItemId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid refund request for order item {}: {}", orderItemId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

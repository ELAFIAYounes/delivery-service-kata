package com.ordermanagement.rest.api.order;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.service.order.OrderService;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController implements OrderAPI {

    private final OrderService orderService;

    @Override
    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<List<Order>> getCustomerOrderHistory(@PathVariable String customerId) {
        log.debug("REST request to get order history for customer: {}", customerId);
        try {
            List<Order> orders = orderService.getCustomerOrderHistory(customerId);
            log.debug("Found {} orders for customer: {}", orders.size(), customerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching order history for customer {}: {}", customerId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @PostMapping("/orders/items/{orderItemId}/refund")
    public ResponseEntity<Void> submitRefundRequest(
        @PathVariable Long orderItemId,
        @Valid @RequestBody RefundRequestDTO refundRequest
    ) {
        log.debug("REST request to submit refund for order item: {}", orderItemId);
        try {
            orderService.submitRefundRequest(
                orderItemId,
                refundRequest.getDescription(),
                refundRequest.getEvidenceImageUrl()
            );
            log.debug("Refund request submitted successfully for order item: {}", orderItemId);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException e) {
            log.error("Order item not found: {}", orderItemId);
            throw e;
        } catch (IllegalStateException e) {
            log.error("Invalid refund request for order item {}: {}", orderItemId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error processing refund request for order item {}: {}", orderItemId, e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException e) {
        ErrorResponse error = new ErrorResponse("ORDER_NOT_FOUND", e.getMessage());
        return ResponseEntity.notFound().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        ErrorResponse error = new ErrorResponse("INVALID_REQUEST", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}

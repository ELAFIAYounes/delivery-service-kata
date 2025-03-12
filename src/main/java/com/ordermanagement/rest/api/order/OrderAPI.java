package com.ordermanagement.rest.api.order;

import com.ordermanagement.domain.entity.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order Management", description = "APIs for managing orders and refund requests")
public interface OrderAPI {

    @Operation(summary = "Get customer order history")
    @GetMapping("/customers/{customerId}/orders")
    ResponseEntity<List<Order>> getCustomerOrderHistory(
        @Parameter(description = "Customer ID") 
        @PathVariable String customerId
    );

    @Operation(summary = "Submit a refund request for an order item")
    @PostMapping("/orders/items/{orderItemId}/refund")
    ResponseEntity<Void> submitRefundRequest(
        @Parameter(description = "Order Item ID") 
        @PathVariable Long orderItemId,
        @Parameter(description = "Refund request details") 
        @RequestBody RefundRequestDTO refundRequest
    );
}

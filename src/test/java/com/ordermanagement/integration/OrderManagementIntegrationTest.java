package com.ordermanagement.integration;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.domain.entity.OrderItem;
import com.ordermanagement.domain.entity.OrderStatus;
import com.ordermanagement.domain.entity.RefundStatus;
import com.ordermanagement.domain.repository.OrderRepository;
import com.ordermanagement.rest.api.order.RefundRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsInAnyOrder;

@AutoConfigureMockMvc
@Transactional
class OrderManagementIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;
    private OrderItem testOrderItem;
    private static final String TEST_CUSTOMER_ID = "customer123";

    @BeforeEach
    void setUp() {
        // Clean up the database
        orderRepository.deleteAll();

        // Create test order
        testOrder = new Order();
        testOrder.setCustomerId(TEST_CUSTOMER_ID);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setStatus(OrderStatus.DELIVERED);

        // Create test order item
        testOrderItem = new OrderItem();
        testOrderItem.setProductId("PROD-001");
        testOrderItem.setProductName("Test Product");
        testOrderItem.setQuantity(1);
        testOrderItem.setPrice(BigDecimal.valueOf(99.99));
        testOrderItem.setOrder(testOrder);

        testOrder.setItems(Arrays.asList(testOrderItem));

        // Save to database
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    void shouldGetCustomerOrderHistory() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/customers/{customerId}/orders", TEST_CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].customerId").value(TEST_CUSTOMER_ID))
                .andExpect(jsonPath("$[0].status").value(OrderStatus.DELIVERED.name()))
                .andExpect(jsonPath("$[0].items[0].productId").value("PROD-001"))
                .andExpect(jsonPath("$[0].items[0].price").value(99.99));
    }

    @Test
    void shouldSubmitRefundRequest() throws Exception {
        // Arrange
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription("Product arrived damaged");
        refundRequest.setEvidenceImageUrl("http://example.com/evidence.jpg");

        // Act & Assert
        mockMvc.perform(post("/orders/items/{orderItemId}/refund", testOrderItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isOk());

        // Verify the database state
        Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        OrderItem updatedItem = updatedOrder.getItems().get(0);
        
        assertNotNull(updatedItem.getRefundRequest());
        assertEquals("Product arrived damaged", updatedItem.getRefundRequest().getDescription());
        assertEquals("http://example.com/evidence.jpg", updatedItem.getRefundRequest().getEvidenceImageUrl());
        assertEquals(RefundStatus.PENDING, updatedItem.getRefundRequest().getStatus());
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrder() throws Exception {
        // Arrange
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription("Product not received");
        refundRequest.setEvidenceImageUrl("http://example.com/evidence.jpg");

        // Act & Assert
        mockMvc.perform(post("/orders/items/{orderItemId}/refund", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"));
    }

    @Test
    void shouldReturnBadRequestForInvalidRefundRequest() throws Exception {
        // Arrange
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/orders/items/{orderItemId}/refund", testOrderItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleOrderLifecycle() throws Exception {
        // Create a new order in PENDING state
        Order newOrder = new Order();
        newOrder.setCustomerId(TEST_CUSTOMER_ID);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(OrderStatus.PENDING);

        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setProductId("PROD-002");
        newOrderItem.setProductName("New Product");
        newOrderItem.setQuantity(2);
        newOrderItem.setPrice(BigDecimal.valueOf(49.99));
        newOrderItem.setOrder(newOrder);

        newOrder.setItems(Arrays.asList(newOrderItem));
        orderRepository.save(newOrder);

        // Verify PENDING state
        mockMvc.perform(get("/customers/{customerId}/orders", TEST_CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status").value(containsInAnyOrder(
                    OrderStatus.DELIVERED.name(), 
                    OrderStatus.PENDING.name()
                )));

        // Update to CONFIRMED
        newOrder.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(newOrder);

        // Verify CONFIRMED state
        mockMvc.perform(get("/customers/{customerId}/orders", TEST_CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status").value(containsInAnyOrder(
                    OrderStatus.DELIVERED.name(), 
                    OrderStatus.CONFIRMED.name()
                )));
    }

    @Test
    void shouldHandleConcurrentRefundRequests() throws Exception {
        // Arrange
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription("Test concurrent requests");
        refundRequest.setEvidenceImageUrl("http://example.com/evidence.jpg");
        String requestJson = objectMapper.writeValueAsString(refundRequest);

        // Act & Assert - Send multiple concurrent requests
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                try {
                    mockMvc.perform(post("/orders/items/{orderItemId}/refund", testOrderItem.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    fail("Concurrent request failed: " + e.getMessage());
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify only one refund request was created
        Order finalOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        OrderItem finalItem = finalOrder.getItems().get(0);
        assertNotNull(finalItem.getRefundRequest());
        assertEquals(1, finalOrder.getItems().stream()
                .filter(item -> item.getRefundRequest() != null)
                .count());
    }
}

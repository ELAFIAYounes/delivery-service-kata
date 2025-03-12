package com.ordermanagement.rest.api.order;

import com.ordermanagement.BaseTest;
import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.domain.entity.OrderItem;
import com.ordermanagement.domain.entity.OrderStatus;
import com.ordermanagement.service.order.OrderService;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class OrderControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private Order testOrder;
    private OrderItem testOrderItem;
    private static final String TEST_CUSTOMER_ID = "customer123";

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(TEST_CUSTOMER_ID);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setStatus(OrderStatus.DELIVERED);

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setProductId("PROD-001");
        testOrderItem.setProductName("Test Product");
        testOrderItem.setQuantity(1);
        testOrderItem.setPrice(BigDecimal.valueOf(99.99));

        testOrder.setItems(Arrays.asList(testOrderItem));
    }

    @Test
    void getCustomerOrderHistory_WhenOrdersExist_ShouldReturnOrders() throws Exception {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getCustomerOrderHistory(TEST_CUSTOMER_ID)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/customers/{customerId}/orders", TEST_CUSTOMER_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].customerId").value(TEST_CUSTOMER_ID))
            .andExpect(jsonPath("$[0].status").value(OrderStatus.DELIVERED.name()));

        verify(orderService).getCustomerOrderHistory(TEST_CUSTOMER_ID);
    }

    @Test
    void getCustomerOrderHistory_WhenNoOrders_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(orderService.getCustomerOrderHistory(TEST_CUSTOMER_ID)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/customers/{customerId}/orders", TEST_CUSTOMER_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(orderService).getCustomerOrderHistory(TEST_CUSTOMER_ID);
    }

    @Test
    void submitRefundRequest_WhenValidRequest_ShouldReturnSuccess() throws Exception {
        // Arrange
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription("Defective product");
        refundRequest.setEvidenceImageUrl("evidence.jpg");

        doNothing().when(orderService).submitRefundRequest(
            eq(testOrderItem.getId()),
            eq(refundRequest.getDescription()),
            eq(refundRequest.getEvidenceImageUrl())
        );

        // Act & Assert
        mockMvc.perform(post("/orders/items/{orderItemId}/refund", testOrderItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
            .andExpect(status().isOk());

        verify(orderService).submitRefundRequest(
            eq(testOrderItem.getId()),
            eq(refundRequest.getDescription()),
            eq(refundRequest.getEvidenceImageUrl())
        );
    }

    @Test
    void submitRefundRequest_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription("Defective product");
        refundRequest.setEvidenceImageUrl("evidence.jpg");

        doThrow(new OrderNotFoundException("Order not found"))
            .when(orderService).submitRefundRequest(any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/orders/items/{orderItemId}/refund", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"));
    }

    @Test
    void submitRefundRequest_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/orders/items/{orderItemId}/refund", testOrderItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refundRequest)))
            .andExpect(status().isBadRequest());
    }
}

package com.ordermanagement.rest.api.order;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.domain.entity.OrderItem;
import com.ordermanagement.domain.entity.OrderStatus;
import com.ordermanagement.service.order.OrderService;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

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
    void getCustomerOrderHistory_ShouldReturnOrders_WhenOrdersExist() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getCustomerOrderHistory(TEST_CUSTOMER_ID)).thenReturn(orders);

        // When
        ResponseEntity<List<Order>> response = orderController.getCustomerOrderHistory(TEST_CUSTOMER_ID);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .isNotNull()
            .hasSize(1)
            .first()
            .hasFieldOrPropertyWithValue("customerId", TEST_CUSTOMER_ID);
        verify(orderService).getCustomerOrderHistory(TEST_CUSTOMER_ID);
    }

    @Test
    void getCustomerOrderHistory_ShouldReturnNotFound_WhenCustomerNotFound() {
        // Given
        when(orderService.getCustomerOrderHistory(TEST_CUSTOMER_ID))
            .thenThrow(new OrderNotFoundException("Customer not found"));

        // When
        ResponseEntity<List<Order>> response = orderController.getCustomerOrderHistory(TEST_CUSTOMER_ID);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(orderService).getCustomerOrderHistory(TEST_CUSTOMER_ID);
    }

    @Test
    void getCustomerOrderHistory_ShouldReturnEmptyList_WhenNoOrders() {
        // Given
        when(orderService.getCustomerOrderHistory(TEST_CUSTOMER_ID)).thenReturn(List.of());

        // When
        ResponseEntity<List<Order>> response = orderController.getCustomerOrderHistory(TEST_CUSTOMER_ID);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .isNotNull()
            .isEmpty();
        verify(orderService).getCustomerOrderHistory(TEST_CUSTOMER_ID);
    }

    @Test
    void submitRefundRequest_ShouldReturnOk_WhenRequestIsValid() {
        // Given
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription("Defective product");
        refundRequest.setEvidenceImageUrl("evidence.jpg");

        // When
        ResponseEntity<Void> response = orderController.submitRefundRequest(testOrderItem.getId(), refundRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(orderService).submitRefundRequest(
            testOrderItem.getId(),
            refundRequest.getDescription(),
            refundRequest.getEvidenceImageUrl()
        );
    }

    @Test
    void submitRefundRequest_ShouldReturnNotFound_WhenOrderNotFound() {
        // Given
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription("Defective product");
        refundRequest.setEvidenceImageUrl("evidence.jpg");

        doThrow(new OrderNotFoundException("Order not found"))
            .when(orderService)
            .submitRefundRequest(anyLong(), anyString(), anyString());

        // When
        ResponseEntity<Void> response = orderController.submitRefundRequest(999L, refundRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(orderService).submitRefundRequest(
            eq(999L),
            eq(refundRequest.getDescription()),
            eq(refundRequest.getEvidenceImageUrl())
        );
    }

    @Test
    void submitRefundRequest_ShouldReturnBadRequest_WhenRequestIsInvalid() {
        // Given
        RefundRequestDTO refundRequest = new RefundRequestDTO();
        refundRequest.setDescription(""); // Invalid description
        refundRequest.setEvidenceImageUrl(""); // Invalid URL

        doThrow(new IllegalArgumentException("Invalid request"))
            .when(orderService)
            .submitRefundRequest(anyLong(), anyString(), anyString());

        // When
        ResponseEntity<Void> response = orderController.submitRefundRequest(testOrderItem.getId(), refundRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(orderService).submitRefundRequest(
            eq(testOrderItem.getId()),
            eq(refundRequest.getDescription()),
            eq(refundRequest.getEvidenceImageUrl())
        );
    }
}

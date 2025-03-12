package com.ordermanagement.service.order;

import com.ordermanagement.BaseTest;
import com.ordermanagement.domain.entity.*;
import com.ordermanagement.domain.repository.OrderRepository;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import com.ordermanagement.service.order.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest extends BaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

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
    void getCustomerOrderHistory_ShouldReturnOrders() {
        // Arrange
        when(orderRepository.findByCustomerIdOrderByOrderDateDesc(TEST_CUSTOMER_ID))
            .thenReturn(Arrays.asList(testOrder));

        // Act
        List<Order> result = orderService.getCustomerOrderHistory(TEST_CUSTOMER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_CUSTOMER_ID, result.get(0).getCustomerId());
        verify(orderRepository).findByCustomerIdOrderByOrderDateDesc(TEST_CUSTOMER_ID);
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));
        verify(orderRepository).findById(999L);
    }

    @Test
    void submitRefundRequest_WhenValidRequest_ShouldCreateRefundRequest() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.submitRefundRequest(
            testOrderItem.getId(),
            "Defective product",
            "evidence.jpg"
        );

        // Assert
        verify(orderRepository).save(any(Order.class));
        assertNotNull(testOrderItem.getRefundRequest());
        assertEquals(RefundStatus.PENDING, testOrderItem.getRefundRequest().getStatus());
        assertEquals("Defective product", testOrderItem.getRefundRequest().getDescription());
        assertEquals("evidence.jpg", testOrderItem.getRefundRequest().getEvidenceImageUrl());
        assertEquals(OrderStatus.DELIVERED, testOrder.getStatus(), "Order status should remain DELIVERED after refund request");
    }

    @Test
    void submitRefundRequest_WhenOrderItemNotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () ->
            orderService.submitRefundRequest(999L, "Invalid item", "evidence.jpg")
        );
    }

    @Test
    void submitRefundRequest_WhenOrderNotDelivered_ShouldThrowException() {
        // Arrange
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
            orderService.submitRefundRequest(
                testOrderItem.getId(),
                "Cannot refund undelivered item",
                "evidence.jpg"
            )
        );
    }

    @Test
    void submitRefundRequest_WhenRefundAlreadyExists_ShouldThrowException() {
        // Arrange
        RefundRequest existingRefund = new RefundRequest();
        existingRefund.setStatus(RefundStatus.PENDING);
        testOrderItem.setRefundRequest(existingRefund);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
            orderService.submitRefundRequest(
                testOrderItem.getId(),
                "Duplicate refund request",
                "evidence.jpg"
            )
        );
    }
}

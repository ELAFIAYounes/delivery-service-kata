package com.ordermanagement.service.order;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.domain.entity.OrderItem;
import com.ordermanagement.domain.entity.OrderStatus;
import com.ordermanagement.domain.entity.RefundRequest;
import com.ordermanagement.domain.repository.OrderRepository;
import com.ordermanagement.domain.repository.OrderItemRepository;
import com.ordermanagement.domain.repository.RefundRequestRepository;
import com.ordermanagement.service.order.exception.OrderNotFoundException;
import com.ordermanagement.service.order.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private RefundRequestRepository refundRequestRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, refundRequestRepository);
    }

    @Test
    void getCustomerOrderHistory_ShouldReturnOrders() {
        // Given
        String customerId = "customer123";
        Order order1 = new Order();
        order1.setId(1L);
        order1.setCustomerId(customerId);
        order1.setStatus(OrderStatus.DELIVERED);
        order1.setOrderDate(LocalDateTime.now().minusDays(1));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomerId(customerId);
        order2.setStatus(OrderStatus.PENDING);
        order2.setOrderDate(LocalDateTime.now());

        List<Order> expectedOrders = Arrays.asList(order1, order2);

        when(orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)).thenReturn(expectedOrders);

        // When
        List<Order> actualOrders = orderService.getCustomerOrderHistory(customerId);

        // Then
        assertThat(actualOrders).hasSize(2);
        assertThat(actualOrders).containsExactlyElementsOf(expectedOrders);
        verify(orderRepository).findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    @Test
    void submitRefundRequest_ShouldCreateRefundRequest() {
        // Given
        Long orderItemId = 1L;
        String description = "Defective product";
        String evidenceImageUrl = "http://example.com/evidence.jpg";

        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.DELIVERED);
        orderItem.setOrder(order);

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        when(refundRequestRepository.save(any(RefundRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = orderService.submitRefundRequest(orderItemId, description, evidenceImageUrl);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(order.getId());
        verify(orderItemRepository).findById(orderItemId);
        verify(refundRequestRepository).save(any(RefundRequest.class));
    }

    @Test
    void submitRefundRequest_ShouldThrowException_WhenOrderItemNotFound() {
        // Given
        Long orderItemId = 1L;
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrderNotFoundException.class, () ->
            orderService.submitRefundRequest(orderItemId, "description", "evidence.jpg"));
        verify(orderItemRepository).findById(orderItemId);
        verify(refundRequestRepository, never()).save(any());
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        // Given
        Long orderId = 1L;
        Order expectedOrder = new Order();
        expectedOrder.setId(orderId);
        expectedOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        // When
        Order actualOrder = orderService.getOrderById(orderId);

        // Then
        assertThat(actualOrder).isNotNull();
        assertThat(actualOrder.getId()).isEqualTo(orderId);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void saveOrder_ShouldReturnSavedOrder() {
        // Given
        Order order = new Order();
        order.setCustomerId("customer123");
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        when(orderRepository.save(order)).thenReturn(order);

        // When
        Order savedOrder = orderService.saveOrder(order);

        // Then
        assertThat(savedOrder).isNotNull();
        verify(orderRepository).save(order);
    }
}

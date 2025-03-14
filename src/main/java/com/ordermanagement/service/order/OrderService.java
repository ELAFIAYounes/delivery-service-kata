package com.ordermanagement.service.order;

import com.ordermanagement.domain.entity.Order;
import java.util.List;

public interface OrderService {
    List<Order> getCustomerOrderHistory(String customerId);
    Order getOrderById(Long orderId);
    Order submitRefundRequest(Long orderItemId, String description, String evidenceImageUrl);
    Order saveOrder(Order order);
}

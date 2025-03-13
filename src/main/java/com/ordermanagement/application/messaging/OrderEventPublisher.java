package com.ordermanagement.application.messaging;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.domain.entity.RefundRequest;

public interface OrderEventPublisher {
    void publishOrderCreated(Order order);
    void publishRefundRequested(RefundRequest refundRequest);
}

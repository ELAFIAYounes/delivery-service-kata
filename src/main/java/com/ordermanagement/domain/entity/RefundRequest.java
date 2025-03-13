package com.ordermanagement.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "refund_requests", uniqueConstraints = @UniqueConstraint(columnNames = "order_item_id"))
@Data
@EqualsAndHashCode(exclude = "orderItem")
@ToString(exclude = "orderItem")
public class RefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "evidence_image_url", nullable = false)
    private String evidenceImageUrl;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
        if (orderItem != null && orderItem.getRefundRequest() != this) {
            orderItem.setRefundRequest(this);
        }
    }
}

package com.delivery.domain.model;

import com.delivery.domain.entity.DeliveryMode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

public class TimeSlotModel extends RepresentationModel<TimeSlotModel> {
    private Long id;
    private DeliveryMode deliveryMode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(DeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

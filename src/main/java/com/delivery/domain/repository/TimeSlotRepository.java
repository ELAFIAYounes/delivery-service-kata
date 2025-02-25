package com.delivery.domain.repository;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByDeliveryModeAndIsAvailable(DeliveryMode mode, boolean isAvailable);
}

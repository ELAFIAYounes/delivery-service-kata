package com.delivery.service.timeslot.impl;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.entity.TimeSlot;
import com.delivery.domain.repository.TimeSlotRepository;
import com.delivery.service.timeslot.TimeSlotService;
import com.delivery.service.validator.TimeSlotValidatorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotServiceImpl.class);

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotValidatorService validatorService;

    @Override
    @Cacheable(value = "deliveryModes")
    public List<DeliveryMode> getAvailableDeliveryModes() {
        return Arrays.asList(DeliveryMode.values());
    }

    @Override
    @Cacheable(value = "timeSlots", key = "#mode")
    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableTimeSlots(DeliveryMode mode) {
        return timeSlotRepository.findByDeliveryModeAndIsAvailable(mode, true);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"timeSlots", "timeSlot"}, allEntries = true)
    public TimeSlot reserveTimeSlot(Long timeSlotId) throws IllegalArgumentException, IllegalStateException {
        TimeSlot timeSlot = getTimeSlotById(timeSlotId);
        if (!timeSlot.isAvailable()) {
            throw new IllegalStateException("Time slot is not available");
        }
        timeSlot.setAvailable(false);
        return timeSlotRepository.save(timeSlot);
    }

    @Override
    @Cacheable(value = "timeSlot", key = "#id")
    @Transactional(readOnly = true)
    public TimeSlot getTimeSlotById(Long id) throws IllegalArgumentException {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found with id: " + id));
    }
}

package com.delivery.service.timeslot;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.entity.TimeSlot;

import java.util.List;

/**
 * Service interface for managing time slots.
 */
public interface TimeSlotService {

    /**
     * Get all available delivery modes.
     *
     * @return List of available delivery modes
     */
    List<DeliveryMode> getAvailableDeliveryModes();

    /**
     * Get available time slots for a specific delivery mode.
     *
     * @param mode The delivery mode to get time slots for
     * @return List of available time slots
     */
    List<TimeSlot> getAvailableTimeSlots(DeliveryMode mode);

    /**
     * Reserve a specific time slot.
     *
     * @param timeSlotId ID of the time slot to reserve
     * @return The reserved time slot
     * @throws IllegalArgumentException if time slot not found
     * @throws IllegalStateException if time slot is not available
     */
    TimeSlot reserveTimeSlot(Long timeSlotId) throws IllegalArgumentException, IllegalStateException;

    /**
     * Get a time slot by its ID.
     *
     * @param id ID of the time slot to retrieve
     * @return The time slot
     * @throws IllegalArgumentException if time slot not found
     */
    TimeSlot getTimeSlotById(Long id) throws IllegalArgumentException;
}

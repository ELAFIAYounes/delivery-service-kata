package com.delivery.service.timeslot.impl;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.entity.TimeSlot;
import com.delivery.domain.repository.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceImplTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private TimeSlotServiceImpl timeSlotService;

    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setStartTime(LocalDateTime.now());
        timeSlot.setEndTime(LocalDateTime.now().plusMinutes(30));
        timeSlot.setDeliveryMode(DeliveryMode.DRIVE);
        timeSlot.setAvailable(true);
    }

    @Test
    void getAvailableDeliveryModes_ReturnsAllModes() {
        List<DeliveryMode> modes = timeSlotService.getAvailableDeliveryModes();
        assertEquals(Arrays.asList(DeliveryMode.values()), modes);
    }

    @Test
    void getAvailableTimeSlots_ReturnsTimeSlots() {
        when(timeSlotRepository.findByDeliveryModeAndIsAvailable(eq(DeliveryMode.DRIVE), eq(true)))
            .thenReturn(Arrays.asList(timeSlot));

        List<TimeSlot> result = timeSlotService.getAvailableTimeSlots(DeliveryMode.DRIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(DeliveryMode.DRIVE, result.get(0).getDeliveryMode());
    }

    @Test
    void reserveTimeSlot_Success() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(eq(timeSlot))).thenAnswer(invocation -> invocation.getArgument(0));

        TimeSlot result = timeSlotService.reserveTimeSlot(1L);

        assertNotNull(result);
        assertFalse(result.isAvailable());
        assertEquals(1L, result.getId());
    }

    @Test
    void reserveTimeSlot_NotFound() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> timeSlotService.reserveTimeSlot(1L));
    }

    @Test
    void reserveTimeSlot_NotAvailable() {
        timeSlot.setAvailable(false);
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

        assertThrows(IllegalStateException.class, () -> timeSlotService.reserveTimeSlot(1L));
    }

    @Test
    void getTimeSlotById_Success() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

        TimeSlot result = timeSlotService.getTimeSlotById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTimeSlotById_NotFound() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> timeSlotService.getTimeSlotById(1L));
    }
}

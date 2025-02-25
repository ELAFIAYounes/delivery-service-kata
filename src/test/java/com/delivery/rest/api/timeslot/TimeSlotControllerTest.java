package com.delivery.rest.api.timeslot;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.entity.TimeSlot;
import com.delivery.domain.mapper.TimeSlotMapper;
import com.delivery.domain.model.TimeSlotModel;
import com.delivery.service.timeslot.TimeSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeSlotControllerTest {

    @Mock
    private TimeSlotService timeSlotService;

    @Mock
    private TimeSlotMapper timeSlotMapper;

    @InjectMocks
    private TimeSlotController timeSlotController;

    private TimeSlot timeSlot;
    private TimeSlotModel timeSlotModel;

    @BeforeEach
    void setUp() {
        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setStartTime(LocalDateTime.now());
        timeSlot.setEndTime(LocalDateTime.now().plusMinutes(30));
        timeSlot.setDeliveryMode(DeliveryMode.DRIVE);
        timeSlot.setAvailable(true);

        timeSlotModel = new TimeSlotModel();
        timeSlotModel.setId(1L);
        timeSlotModel.setStartTime(timeSlot.getStartTime());
        timeSlotModel.setEndTime(timeSlot.getEndTime());
        timeSlotModel.setDeliveryMode(timeSlot.getDeliveryMode());
        timeSlotModel.setAvailable(timeSlot.isAvailable());
    }

    @Test
    void getDeliveryModes_ReturnsAvailableModes() {
        List<DeliveryMode> modes = Arrays.asList(DeliveryMode.values());
        when(timeSlotService.getAvailableDeliveryModes()).thenReturn(modes);

        ResponseEntity<CollectionModel<DeliveryMode>> response = timeSlotController.getDeliveryModes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().containsAll(modes));
    }

    @Test
    void getAvailableTimeSlots_ReturnsTimeSlots() {
        List<TimeSlot> timeSlots = Arrays.asList(timeSlot);
        when(timeSlotService.getAvailableTimeSlots(eq(DeliveryMode.DRIVE))).thenReturn(timeSlots);
        when(timeSlotMapper.toModel(eq(timeSlot))).thenReturn(timeSlotModel);

        ResponseEntity<CollectionModel<TimeSlotModel>> response = 
            timeSlotController.getAvailableTimeSlots(DeliveryMode.DRIVE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().iterator().hasNext());
    }

    @Test
    void reserveTimeSlot_Success() {
        when(timeSlotService.reserveTimeSlot(1L)).thenReturn(timeSlot);
        when(timeSlotMapper.toModel(timeSlot)).thenReturn(timeSlotModel);

        ResponseEntity<TimeSlotModel> response = timeSlotController.reserveTimeSlot(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void reserveTimeSlot_NotFound() {
        when(timeSlotService.reserveTimeSlot(1L)).thenThrow(new IllegalArgumentException("Time slot not found"));

        ResponseEntity<TimeSlotModel> response = timeSlotController.reserveTimeSlot(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void reserveTimeSlot_NotAvailable() {
        when(timeSlotService.reserveTimeSlot(1L)).thenThrow(new IllegalStateException("Time slot not available"));

        ResponseEntity<TimeSlotModel> response = timeSlotController.reserveTimeSlot(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

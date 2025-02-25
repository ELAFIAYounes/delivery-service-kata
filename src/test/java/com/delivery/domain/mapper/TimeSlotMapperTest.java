package com.delivery.domain.mapper;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.entity.TimeSlot;
import com.delivery.domain.model.TimeSlotModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimeSlotMapperTest {

    private TimeSlotMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TimeSlotMapper();
    }

    @Test
    void shouldMapTimeSlotToModel() {
        // Given
        TimeSlot entity = new TimeSlot();
        entity.setId(1L);
        entity.setStartTime(LocalDateTime.now());
        entity.setEndTime(LocalDateTime.now().plusHours(1));
        entity.setDeliveryMode(DeliveryMode.DELIVERY);
        entity.setAvailable(true);

        // When
        TimeSlotModel model = mapper.toModel(entity);

        // Then
        assertEquals(entity.getId(), model.getId());
        assertEquals(entity.getStartTime(), model.getStartTime());
        assertEquals(entity.getEndTime(), model.getEndTime());
        assertEquals(entity.getDeliveryMode(), model.getDeliveryMode());
        assertEquals(entity.isAvailable(), model.isAvailable());
    }

    @Test
    void shouldMapModelToTimeSlot() {
        // Given
        TimeSlotModel model = new TimeSlotModel();
        model.setId(1L);
        model.setStartTime(LocalDateTime.now());
        model.setEndTime(LocalDateTime.now().plusHours(1));
        model.setDeliveryMode(DeliveryMode.DELIVERY);
        model.setAvailable(true);

        // When
        TimeSlot entity = mapper.toEntity(model);

        // Then
        assertEquals(model.getId(), entity.getId());
        assertEquals(model.getStartTime(), entity.getStartTime());
        assertEquals(model.getEndTime(), entity.getEndTime());
        assertEquals(model.getDeliveryMode(), entity.getDeliveryMode());
        assertEquals(model.isAvailable(), entity.isAvailable());
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void shouldReturnNullWhenMappingNullModel() {
        assertNull(mapper.toEntity(null));
    }
}

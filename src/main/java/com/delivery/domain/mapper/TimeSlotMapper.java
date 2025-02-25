package com.delivery.domain.mapper;

import com.delivery.domain.entity.TimeSlot;
import com.delivery.domain.model.TimeSlotModel;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper {
    
    public TimeSlotModel toModel(TimeSlot entity) {
        if (entity == null) {
            return null;
        }

        TimeSlotModel model = new TimeSlotModel();
        model.setId(entity.getId());
        model.setStartTime(entity.getStartTime());
        model.setEndTime(entity.getEndTime());
        model.setDeliveryMode(entity.getDeliveryMode());
        model.setAvailable(entity.isAvailable());
        return model;
    }

    public TimeSlot toEntity(TimeSlotModel model) {
        if (model == null) {
            return null;
        }

        TimeSlot entity = new TimeSlot();
        entity.setId(model.getId());
        entity.setStartTime(model.getStartTime());
        entity.setEndTime(model.getEndTime());
        entity.setDeliveryMode(model.getDeliveryMode());
        entity.setAvailable(model.isAvailable());
        return entity;
    }
}

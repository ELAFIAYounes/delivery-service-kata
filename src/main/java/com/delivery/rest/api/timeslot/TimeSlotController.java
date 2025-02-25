package com.delivery.rest.api.timeslot;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.entity.TimeSlot;
import com.delivery.domain.mapper.TimeSlotMapper;
import com.delivery.domain.model.TimeSlotModel;
import com.delivery.service.timeslot.TimeSlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TimeSlotController implements TimeSlotAPI {
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotController.class);

    private final TimeSlotService timeSlotService;
    private final TimeSlotMapper timeSlotMapper;

    public TimeSlotController(TimeSlotService timeSlotService, TimeSlotMapper timeSlotMapper) {
        this.timeSlotService = timeSlotService;
        this.timeSlotMapper = timeSlotMapper;
    }

    @Override
    public ResponseEntity<CollectionModel<DeliveryMode>> getDeliveryModes() {
        logger.debug("Getting available delivery modes");
        List<DeliveryMode> modes = timeSlotService.getAvailableDeliveryModes();
        CollectionModel<DeliveryMode> collectionModel = CollectionModel.of(modes);
        collectionModel.add(linkTo(methodOn(TimeSlotController.class).getDeliveryModes()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    @Override
    public ResponseEntity<CollectionModel<TimeSlotModel>> getAvailableTimeSlots(DeliveryMode deliveryMode) {
        logger.debug("Getting available time slots for mode: {}", deliveryMode);
        List<TimeSlotModel> timeSlots = timeSlotService.getAvailableTimeSlots(deliveryMode)
                .stream()
                .map(timeSlotMapper::toModel)
                .map(this::addTimeSlotLinks)
                .collect(Collectors.toList());

        CollectionModel<TimeSlotModel> collectionModel = CollectionModel.of(timeSlots);
        collectionModel.add(linkTo(methodOn(TimeSlotController.class).getAvailableTimeSlots(deliveryMode)).withSelfRel());
        collectionModel.add(linkTo(methodOn(TimeSlotController.class).getDeliveryModes()).withRel("deliveryModes"));
        
        return ResponseEntity.ok(collectionModel);
    }

    @Override
    public ResponseEntity<TimeSlotModel> reserveTimeSlot(Long timeSlotId) {
        logger.debug("Reserving time slot: {}", timeSlotId);
        try {
            TimeSlot reservedSlot = timeSlotService.reserveTimeSlot(timeSlotId);
            TimeSlotModel timeSlotModel = timeSlotMapper.toModel(reservedSlot);
            return ResponseEntity.ok(addTimeSlotLinks(timeSlotModel));
        } catch (IllegalArgumentException e) {
            logger.warn("Time slot not found: {}", timeSlotId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            logger.warn("Time slot {} is not available", timeSlotId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private TimeSlotModel addTimeSlotLinks(TimeSlotModel model) {
        model.add(linkTo(methodOn(TimeSlotController.class).reserveTimeSlot(model.getId())).withSelfRel());
        if (model.isAvailable()) {
            model.add(linkTo(methodOn(TimeSlotController.class).reserveTimeSlot(model.getId())).withRel("reserve"));
        }
        model.add(linkTo(methodOn(TimeSlotController.class).getAvailableTimeSlots(model.getDeliveryMode())).withRel("timeSlots"));
        return model;
    }
}

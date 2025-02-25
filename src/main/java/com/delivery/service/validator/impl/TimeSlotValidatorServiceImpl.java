package com.delivery.service.validator.impl;

import com.delivery.domain.entity.TimeSlot;
import com.delivery.service.validator.TimeSlotValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TimeSlotValidatorServiceImpl implements TimeSlotValidatorService {
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotValidatorServiceImpl.class);

    @Override
    public ValidationResult validateTimeSlot(TimeSlot slot) {
        logger.debug("Validating time slot with mode: {}", slot.getDeliveryMode());
        return switch (slot.getDeliveryMode()) {
            case DRIVE -> validateDriveSlot(slot);
            case DELIVERY -> validateDeliverySlot(slot);
            case DELIVERY_TODAY -> validateDeliveryTodaySlot(slot);
            case DELIVERY_ASAP -> validateDeliveryASAPSlot(slot);
        };
    }

    private ValidationResult validateDriveSlot(TimeSlot slot) {
        if (slot instanceof TimeSlot ts && ts.getStartTime() != null && ts.getEndTime() != null) {
            long minutes = ChronoUnit.MINUTES.between(ts.getStartTime(), ts.getEndTime());
            logger.debug("Drive slot duration: {} minutes", minutes);
            if (minutes == 30) {
                logger.debug("Drive slot validation successful");
                return ValidationResult.success();
            }
            logger.warn("Drive slot validation failed: duration is not 30 minutes");
            return ValidationResult.error("Drive slots must be exactly 30 minutes");
        }
        logger.error("Invalid time slot: missing start or end time");
        return ValidationResult.error("Invalid time slot");
    }

    private ValidationResult validateDeliverySlot(TimeSlot slot) {
        if (slot instanceof TimeSlot ts && ts.getStartTime() != null) {
            boolean isValid = ts.getStartTime().isAfter(LocalDateTime.now().plusHours(24));
            logger.debug("Delivery slot scheduled for: {}, isValid: {}", ts.getStartTime(), isValid);
            if (isValid) {
                return ValidationResult.success();
            }
            logger.warn("Delivery slot validation failed: not scheduled 24 hours in advance");
            return ValidationResult.error("Delivery must be scheduled at least 24 hours in advance");
        }
        logger.error("Invalid delivery slot: missing start time");
        return ValidationResult.error("Invalid time slot");
    }

    private ValidationResult validateDeliveryTodaySlot(TimeSlot slot) {
        if (slot instanceof TimeSlot ts && ts.getStartTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            boolean isValid = ts.getStartTime().toLocalDate().equals(now.toLocalDate());
            logger.debug("Delivery today slot date: {}, isValid: {}", ts.getStartTime().toLocalDate(), isValid);
            if (isValid) {
                return ValidationResult.success();
            }
            logger.warn("Delivery today slot validation failed: not scheduled for today");
            return ValidationResult.error("Delivery today slots must be for today");
        }
        logger.error("Invalid delivery today slot: missing start time");
        return ValidationResult.error("Invalid time slot");
    }

    private ValidationResult validateDeliveryASAPSlot(TimeSlot slot) {
        if (slot instanceof TimeSlot ts && ts.getStartTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            long minutes = ChronoUnit.MINUTES.between(now, ts.getStartTime());
            logger.debug("ASAP delivery minutes from now: {}", minutes);
            if (minutes <= 120) {
                return ValidationResult.success();
            }
            logger.warn("ASAP delivery validation failed: not within 2 hours");
            return ValidationResult.error("ASAP delivery must be within 2 hours");
        }
        logger.error("Invalid ASAP delivery slot: missing start time");
        return ValidationResult.error("Invalid time slot");
    }
}

package com.delivery.service.validator;

import com.delivery.domain.entity.TimeSlot;

public interface TimeSlotValidatorService {
    record ValidationResult(boolean valid, String message) {
        public static ValidationResult success() {
            return new ValidationResult(true, "Valid");
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }

    ValidationResult validateTimeSlot(TimeSlot slot);
}

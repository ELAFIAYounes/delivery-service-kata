package com.delivery.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeliveryMode {
    DRIVE,
    DELIVERY,
    DELIVERY_TODAY,
    DELIVERY_ASAP;

    @JsonValue
    public String getValue() {
        return name();
    }
}

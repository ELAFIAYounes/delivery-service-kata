package com.ordermanagement.rest.api.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefundRequestDTO {
    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Evidence image URL is required")
    private String evidenceImageUrl;
}

package com.ordermanagement.rest.api.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> details;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

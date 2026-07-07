package gr.aueb.cf.doctor_app.dto;

import java.util.Map;

public record ValidationErrorResponseDTO(
        String code, String message, Map<String, String> errors
) {}
package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Request to create a service type")
public record CreateServiceTypeRequest(
        @NotBlank(message = "Service type name is required")
        @Schema(description = "Service type name", example = "Full Body Wash")
        String name,

        @Schema(description = "Service description", example = "Complete exterior and interior car wash")
        String description,

        @NotNull(message = "Base price is required")
        @Schema(description = "Base price for the service", example = "25.00")
        BigDecimal basePrice,

        @Schema(description = "Estimated duration in minutes", example = "60")
        Integer estimatedDurationMinutes,

        @NotNull(message = "Service category ID is required")
        @Schema(description = "Service category ID")
        UUID serviceCategoryId
) {}
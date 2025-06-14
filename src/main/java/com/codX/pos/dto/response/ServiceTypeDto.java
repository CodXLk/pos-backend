package com.codX.pos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Service type details")
public record ServiceTypeDto(
        @Schema(description = "Service type ID")
        UUID id,

        @Schema(description = "Service type name")
        String name,

        @Schema(description = "Service type description")
        String description,

        @Schema(description = "Base price")
        BigDecimal basePrice,

        @Schema(description = "Estimated duration in minutes")
        Integer estimatedDurationMinutes,

        @Schema(description = "Service category ID")
        UUID serviceCategoryId
) {}

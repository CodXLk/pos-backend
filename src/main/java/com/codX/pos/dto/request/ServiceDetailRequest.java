package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Service detail request")
public record ServiceDetailRequest(
        @NotNull(message = "Service type ID is required")
        @Schema(description = "Service type ID")
        UUID serviceTypeId,

        @Schema(description = "Item ID (optional)")
        UUID itemId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        @Schema(description = "Quantity", example = "1")
        Integer quantity,

        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be positive")
        @Schema(description = "Unit price", example = "25.00")
        BigDecimal unitPrice,

        @Schema(description = "Notes for this service detail")
        String notes
) {}

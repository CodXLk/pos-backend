package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Service detail request")
public record ServiceDetailRequest(
        @NotNull(message = "Service type ID is required")
        @Schema(description = "Service type ID")
        UUID serviceTypeId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        @Schema(description = "Service quantity", example = "1")
        Integer quantity,

        @Schema(description = "Unit price override (optional)")
        BigDecimal unitPrice,

        @Schema(description = "Notes for this service")
        String notes,

        @Schema(description = "Items used in this service")
        List<ServiceItemRequest> items
) {}

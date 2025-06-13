package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Service item request")
public record ServiceItemRequest(
        @NotNull(message = "Item ID is required")
        @Schema(description = "Item ID")
        UUID itemId,

        @NotNull(message = "Quantity is required")
        @Schema(description = "Quantity used", example = "2")
        Integer quantity,

        @Schema(description = "Unit price")
        BigDecimal unitPrice
) {}
package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Invoice item request")
public record InvoiceItemRequest(
        @NotNull(message = "Item ID is required")
        @Schema(description = "Item ID")
        UUID itemId,

        @NotNull(message = "Quantity is required")
        @Schema(description = "Quantity", example = "1")
        Integer quantity,

        @Schema(description = "Unit price")
        BigDecimal unitPrice
) {}
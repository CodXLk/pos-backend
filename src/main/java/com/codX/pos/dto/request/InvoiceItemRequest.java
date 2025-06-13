package com.codX.pos.dto.request;

import com.codX.pos.entity.InvoiceItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Invoice item request")
public record InvoiceItemRequest(
        @Schema(description = "Item ID (for item sales)")
        UUID itemId,

        @Schema(description = "Service type ID (for service sales)")
        UUID serviceTypeId,

        @NotNull(message = "Quantity is required")
        @Schema(description = "Quantity", example = "1")
        Integer quantity,

        @Schema(description = "Unit price")
        BigDecimal unitPrice,

        @NotNull(message = "Type is required")
        @Schema(description = "Invoice item type")
        InvoiceItemType type
) {}
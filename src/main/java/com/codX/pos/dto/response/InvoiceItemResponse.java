package com.codX.pos.dto.response;

import com.codX.pos.entity.InvoiceItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Invoice item response")
public record InvoiceItemResponse(
        @Schema(description = "Invoice item ID")
        UUID id,

        @Schema(description = "Item/Service description")
        String description,

        @Schema(description = "Quantity")
        Integer quantity,

        @Schema(description = "Unit price")
        BigDecimal unitPrice,

        @Schema(description = "Total price")
        BigDecimal totalPrice,

        @Schema(description = "Item type")
        InvoiceItemType type
) {}
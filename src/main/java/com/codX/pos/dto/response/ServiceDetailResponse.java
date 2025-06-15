package com.codX.pos.dto.response;

import com.codX.pos.entity.ServiceDetailType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Service detail response")
public record ServiceDetailResponse(
        @Schema(description = "Service detail ID")
        UUID id,

        @Schema(description = "Service type ID")
        UUID serviceTypeId,

        @Schema(description = "Service type name")
        String serviceTypeName,

        @Schema(description = "Item ID (if applicable)")
        UUID itemId,

        @Schema(description = "Item name (if applicable)")
        String itemName,

        @Schema(description = "Quantity")
        Integer quantity,

        @Schema(description = "Unit price")
        BigDecimal unitPrice,

        @Schema(description = "Total price")
        BigDecimal totalPrice,

        @Schema(description = "Detail type")
        ServiceDetailType type,

        @Schema(description = "Notes")
        String notes
) {}

package com.codX.pos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Service detail response")
public record ServiceDetailResponse(
        @Schema(description = "Service detail ID")
        UUID id,

        @Schema(description = "Service record ID")
        UUID serviceRecordId,

        @Schema(description = "Service type details")
        ServiceTypeDto serviceType,

        @Schema(description = "Item details")
        ItemDto item,

        @Schema(description = "Quantity")
        Integer quantity,

        @Schema(description = "Unit price")
        BigDecimal unitPrice,

        @Schema(description = "Total price")
        BigDecimal totalPrice,

        @Schema(description = "Notes")
        String notes,

        @Schema(description = "Creation date")
        LocalDateTime createdDate
) {}

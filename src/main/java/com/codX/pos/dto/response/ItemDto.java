package com.codX.pos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Item details")
public record ItemDto(
        @Schema(description = "Item ID")
        UUID id,

        @Schema(description = "Item name")
        String name,

        @Schema(description = "Item description")
        String description,

        @Schema(description = "Unit price")
        BigDecimal unitPrice,

        @Schema(description = "Unit of measurement")
        String unit,

        @Schema(description = "Current stock quantity")
        Integer stockQuantity,

        @Schema(description = "Minimum stock level")
        Integer minStockLevel,

        @Schema(description = "Item category ID")
        UUID itemCategoryId
) {}

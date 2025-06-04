package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Request to create an item")
public record CreateItemRequest(
        @NotBlank(message = "Item name is required")
        @Schema(description = "Item name", example = "Engine Oil 5W-30")
        String name,

        @Schema(description = "Item description", example = "Premium synthetic engine oil")
        String description,

        @NotNull(message = "Unit price is required")
        @Schema(description = "Unit price", example = "12.50")
        BigDecimal unitPrice,

        @NotBlank(message = "Unit is required")
        @Schema(description = "Unit of measurement", example = "liters")
        String unit,

        @Schema(description = "Stock quantity", example = "100")
        Integer stockQuantity,

        @Schema(description = "Minimum stock level", example = "10")
        Integer minStockLevel,

        @NotNull(message = "Item category ID is required")
        @Schema(description = "Item category ID")
        UUID itemCategoryId
) {}
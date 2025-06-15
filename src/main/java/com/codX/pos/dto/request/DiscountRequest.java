package com.codX.pos.dto.request;

import com.codX.pos.entity.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
@Schema(description = "Discount configuration request")
public record DiscountRequest(
        @NotNull(message = "Discount type is required")
        @Schema(description = "Discount type - PERCENTAGE or FIXED_AMOUNT")
        DiscountType type,

        @NotNull(message = "Discount value is required")
        @Schema(description = "Discount value (percentage or fixed amount)")
        BigDecimal value,

        @Schema(description = "Discount description")
        String description
) {}

package com.codX.pos.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Discount type enumeration")
public enum DiscountType {
    @Schema(description = "Percentage-based discount")
    PERCENTAGE,

    @Schema(description = "Fixed amount discount")
    FIXED_AMOUNT
}

package com.codX.pos.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Invoice type enumeration")
public enum InvoiceType {
    @Schema(description = "Service-only invoice")
    SERVICE,

    @Schema(description = "Item sale invoice")
    ITEM_SALE,

    @Schema(description = "Mixed service and item invoice")
    MIXED
}

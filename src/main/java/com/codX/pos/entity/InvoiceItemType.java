package com.codX.pos.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Invoice item type enumeration")
public enum InvoiceItemType {
    @Schema(description = "Service item")
    SERVICE,

    @Schema(description = "Physical item")
    ITEM
}

package com.codX.pos.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Invoice status enumeration")
public enum InvoiceStatus {
    @Schema(description = "Invoice is in draft state")
    DRAFT,

    @Schema(description = "Invoice has been sent to customer")
    SENT,

    @Schema(description = "Invoice has been paid")
    PAID,

    @Schema(description = "Invoice has been cancelled")
    CANCELLED,

    @Schema(description = "Invoice is overdue")
    OVERDUE
}

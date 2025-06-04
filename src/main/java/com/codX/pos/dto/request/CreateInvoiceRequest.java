package com.codX.pos.dto.request;

import com.codX.pos.entity.InvoiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Request to create an invoice")
public record CreateInvoiceRequest(
        @NotNull(message = "Customer ID is required")
        @Schema(description = "Customer ID")
        UUID customerId,

        @Schema(description = "Vehicle ID (for service invoices)")
        UUID vehicleId,

        @Schema(description = "Service record ID (for service invoices)")
        UUID serviceRecordId,

        @NotNull(message = "Invoice type is required")
        @Schema(description = "Invoice type")
        InvoiceType type,

        @Schema(description = "Discount amount")
        BigDecimal discountAmount,

        @Schema(description = "Tax percentage")
        BigDecimal taxPercentage,

        @Schema(description = "Invoice items for item sales")
        List<InvoiceItemRequest> items
) {}

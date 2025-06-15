package com.codX.pos.dto.response;

import com.codX.pos.entity.DiscountType;
import com.codX.pos.entity.InvoiceStatus;
import com.codX.pos.entity.InvoiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Invoice response")
public record InvoiceResponse(
        @Schema(description = "Invoice ID")
        UUID id,

        @Schema(description = "Invoice number")
        String invoiceNumber,

        @Schema(description = "Invoice date")
        LocalDateTime invoiceDate,

        @Schema(description = "Subtotal")
        BigDecimal subtotal,

        @Schema(description = "Tax amount")
        BigDecimal taxAmount,

        @Schema(description = "Discount amount")
        BigDecimal discountAmount,

        @Schema(description = "Overall discount value")
        BigDecimal overallDiscountValue,

        @Schema(description = "Overall discount type")
        DiscountType overallDiscountType,

        @Schema(description = "Overall discount amount")
        BigDecimal overallDiscountAmount,

        @Schema(description = "Total amount")
        BigDecimal totalAmount,

        @Schema(description = "Invoice status")
        InvoiceStatus status,

        @Schema(description = "Invoice type")
        InvoiceType type,

        @Schema(description = "Customer ID")
        UUID customerId,

        @Schema(description = "Vehicle ID")
        UUID vehicleId,

        @Schema(description = "Service record ID")
        UUID serviceRecordId,

        @Schema(description = "Invoice items")
        List<InvoiceItemResponse> items
) {}

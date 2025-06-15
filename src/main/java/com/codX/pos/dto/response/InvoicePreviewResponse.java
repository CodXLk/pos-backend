package com.codX.pos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
@Schema(description = "Invoice preview with discount calculations")
public record InvoicePreviewResponse(
        @Schema(description = "Subtotal before tax")
        BigDecimal subtotal,

        @Schema(description = "Tax amount")
        BigDecimal taxAmount,

        @Schema(description = "Total amount")
        BigDecimal totalAmount,

        @Schema(description = "Estimated savings from discounts")
        BigDecimal estimatedSavings
) {}

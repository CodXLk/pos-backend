package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Request to update invoice discounts")
public record UpdateInvoiceDiscountRequest(
        @Schema(description = "Overall invoice discount")
        DiscountRequest invoiceDiscount,

        @Schema(description = "Individual item discounts")
        List<ItemDiscountRequest> itemDiscounts,

        @Schema(description = "Individual service discounts")
        List<ServiceDiscountRequest> serviceDiscounts
) {}
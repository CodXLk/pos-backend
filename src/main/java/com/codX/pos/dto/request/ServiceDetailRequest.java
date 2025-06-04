package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Service detail request")
public record ServiceDetailRequest(
        @NotNull(message = "Service type ID is required")
        @Schema(description = "Service type ID")
        UUID serviceTypeId,

        @Schema(description = "Service price")
        BigDecimal servicePrice,

        @Schema(description = "Service notes")
        String notes,

        @Schema(description = "Items used in this service")
        List<ServiceItemRequest> items
) {}
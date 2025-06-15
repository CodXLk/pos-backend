package com.codX.pos.dto.response;

import com.codX.pos.entity.ServiceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Service record response")
public record ServiceRecordResponse(
        @Schema(description = "Service record ID")
        UUID id,

        @Schema(description = "Vehicle ID")
        UUID vehicleId,

        @Schema(description = "Customer ID")
        UUID customerId,

        @Schema(description = "Service date")
        LocalDateTime serviceDate,

        @Schema(description = "Current mileage")
        Integer currentMileage,

        @Schema(description = "Service notes")
        String notes,

        @Schema(description = "Service status")
        ServiceStatus status,

        @Schema(description = "Total amount")
        BigDecimal totalAmount,

        @Schema(description = "Invoice ID if created")
        UUID invoiceId
) {}

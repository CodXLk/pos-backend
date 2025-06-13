package com.codX.pos.dto.request;

import com.codX.pos.entity.ServiceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Request to create a service record")
public record CreateServiceRecordRequest(
        @NotNull(message = "Vehicle ID is required")
        @Schema(description = "Vehicle ID")
        UUID vehicleId,

        @NotNull(message = "Customer ID is required")
        @Schema(description = "Customer ID")
        UUID customerId,

        @Schema(description = "Service date", example = "2024-01-15T10:30:00")
        LocalDateTime serviceDate,

        @Schema(description = "Current vehicle mileage", example = "50000")
        Integer currentMileage,

        @Schema(description = "Service notes")
        String notes,

        @Schema(description = "Service status", example = "PENDING")
        ServiceStatus status,

        @Schema(description = "List of services to be performed")
        List<ServiceDetailRequest> serviceDetails
) {}
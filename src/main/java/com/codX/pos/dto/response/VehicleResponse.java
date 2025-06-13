package com.codX.pos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Vehicle information response")
public record VehicleResponse(
        @Schema(description = "Vehicle ID")
        UUID id,

        @Schema(description = "Vehicle number plate")
        String vehicleNumber,

        @Schema(description = "Vehicle make")
        String make,

        @Schema(description = "Vehicle model")
        String model,

        @Schema(description = "Manufacturing year")
        Integer year,

        @Schema(description = "Vehicle color")
        String color,

        @Schema(description = "Engine number")
        String engineNumber,

        @Schema(description = "Chassis number")
        String chassisNumber,

        @Schema(description = "Customer ID")
        UUID customerId,

        @Schema(description = "Creation date")
        LocalDateTime createdDate
) {}
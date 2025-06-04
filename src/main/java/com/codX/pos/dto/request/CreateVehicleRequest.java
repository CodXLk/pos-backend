package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Request to create a new vehicle")
public record CreateVehicleRequest(
        @NotBlank(message = "Vehicle number is required")
        @Schema(description = "Vehicle number plate", example = "ABC-1234")
        String vehicleNumber,

        @NotBlank(message = "Make is required")
        @Schema(description = "Vehicle make", example = "Toyota")
        String make,

        @NotBlank(message = "Model is required")
        @Schema(description = "Vehicle model", example = "Corolla")
        String model,

        @Schema(description = "Manufacturing year", example = "2020")
        Integer year,

        @Schema(description = "Vehicle color", example = "White")
        String color,

        @Schema(description = "Engine number", example = "ENG123456")
        String engineNumber,

        @Schema(description = "Chassis number", example = "CHS789012")
        String chassisNumber,

        @NotNull(message = "Customer ID is required")
        @Schema(description = "Customer ID who owns the vehicle")
        UUID customerId
) {}
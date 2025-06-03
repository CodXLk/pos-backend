package com.codX.pos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(
        description = "Branch information for company locations",
        example = """
        {
            "name": "Main Branch",
            "address": "456 Main Street, City",
            "contactNumber": "+1234567891",
            "companyId": "123e4567-e89b-12d3-a456-426614174000",
            "branchAdminId": "123e4567-e89b-12d3-a456-426614174002"
        }
        """
)
public record Branch(
        @Schema(description = "Branch unique identifier", example = "123e4567-e89b-12d3-a456-426614174001", format = "uuid")
        UUID id,

        @NotBlank(message = "Branch name is required")
        @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters")
        @Schema(
                description = "Branch name",
                example = "Main Branch",
                required = true,
                minLength = 2,
                maxLength = 100
        )
        String name,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        @Schema(
                description = "Branch physical address",
                example = "456 Main Street, City",
                required = true,
                maxLength = 255
        )
        String address,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
        @Schema(
                description = "Branch contact phone number",
                example = "+1234567891",
                required = true,
                pattern = "^\\+?[1-9]\\d{1,14}$"
        )
        String contactNumber,

        @NotNull(message = "Company ID is required")
        @Schema(
                description = "ID of the company this branch belongs to",
                example = "123e4567-e89b-12d3-a456-426614174000",
                required = true,
                format = "uuid"
        )
        UUID companyId,

        @Schema(
                description = "ID of the branch administrator (optional)",
                example = "123e4567-e89b-12d3-a456-426614174002",
                format = "uuid"
        )
        UUID branchAdminId,

        @Schema(description = "Branch active status", example = "true", defaultValue = "true")
        boolean isActive
) {}

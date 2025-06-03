package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(
        description = "Request to create a new branch for a company",
        example = """
        {
            "name": "Downtown Branch",
            "address": "789 Downtown Avenue, Business District",
            "contactNumber": "+1234567892",
            "companyId": "123e4567-e89b-12d3-a456-426614174000",
            "branchAdminId": "123e4567-e89b-12d3-a456-426614174003"
        }
        """
)
public record CreateBranchRequest(
        @NotBlank(message = "Branch name is required")
        @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters")
        @Schema(
                description = "Name of the branch",
                example = "Downtown Branch",
                required = true,
                minLength = 2,
                maxLength = 100
        )
        String name,

        @NotBlank(message = "Address is required")
        @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
        @Schema(
                description = "Physical address of the branch",
                example = "789 Downtown Avenue, Business District",
                required = true,
                minLength = 10,
                maxLength = 255
        )
        String address,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
        @Schema(
                description = "Contact phone number for the branch (international format preferred)",
                example = "+1234567892",
                required = true,
                pattern = "^\\+?[1-9]\\d{1,14}$"
        )
        String contactNumber,

        @Schema(
                description = "Company ID (required for Super Admin, ignored for Company Admin as it uses their context)",
                example = "123e4567-e89b-12d3-a456-426614174000",
                format = "uuid"
        )
        UUID companyId,

        @Schema(
                description = "UUID of the user who will be assigned as branch administrator (optional)",
                example = "123e4567-e89b-12d3-a456-426614174003",
                format = "uuid",
                nullable = true
        )
        UUID branchAdminId
) {}

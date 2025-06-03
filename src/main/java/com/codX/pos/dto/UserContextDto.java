package com.codX.pos.dto;

import com.codX.pos.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(
        description = "User context information extracted from JWT token for request processing",
        example = """
        {
            "userId": "123e4567-e89b-12d3-a456-426614174004",
            "username": "johndoe",
            "role": "COMPANY_ADMIN",
            "companyId": "123e4567-e89b-12d3-a456-426614174000",
            "branchId": "123e4567-e89b-12d3-a456-426614174001"
        }
        """
)
public record UserContextDto(
        @Schema(
                description = "Unique identifier of the authenticated user",
                example = "123e4567-e89b-12d3-a456-426614174004",
                format = "uuid",
                required = true
        )
        UUID userId,

        @Schema(
                description = "Username of the authenticated user",
                example = "johndoe",
                required = true
        )
        String username,

        @Schema(
                description = "Role of the authenticated user",
                example = "COMPANY_ADMIN",
                required = true,
                allowableValues = {"SUPER_ADMIN", "COMPANY_ADMIN", "BRANCH_ADMIN", "POS_USER", "EMPLOYEE", "CUSTOMER"}
        )
        Role role,

        @Schema(
                description = "Company ID associated with the user (null for Super Admin)",
                example = "123e4567-e89b-12d3-a456-426614174000",
                format = "uuid",
                nullable = true
        )
        UUID companyId,

        @Schema(
                description = "Branch ID associated with the user (null for Super Admin and Company Admin)",
                example = "123e4567-e89b-12d3-a456-426614174001",
                format = "uuid",
                nullable = true
        )
        UUID branchId
) {}

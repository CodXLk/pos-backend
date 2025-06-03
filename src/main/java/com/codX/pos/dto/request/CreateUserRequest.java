package com.codX.pos.dto.request;

import com.codX.pos.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(
        description = "Request to create a new user with role-based restrictions and organizational hierarchy",
        example = """
        {
            "firstName": "Jane",
            "lastName": "Smith",
            "userName": "janesmith",
            "phoneNumber": "+1234567890",
            "email": "john.doe@example.com",
            "role": "BRANCH_ADMIN",
            "companyId": "123e4567-e89b-12d3-a456-426614174000",
            "branchId": "123e4567-e89b-12d3-a456-426614174001"
        }
        """
)
public record CreateUserRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Schema(
                description = "User's first name",
                example = "Jane",
                required = true,
                minLength = 2,
                maxLength = 50
        )
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Schema(
                description = "User's last name",
                example = "Smith",
                required = true,
                minLength = 2,
                maxLength = 50
        )
        String lastName,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
        @Schema(
                description = "Unique username for login",
                example = "janesmith",
                required = true,
                pattern = "^[a-zA-Z0-9_]+$",
                minLength = 3,
                maxLength = 30
        )
        String userName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
        @Schema(
                description = "Phone number for OTP verification (international format preferred)",
                example = "+1234567890",
                required = true,
                pattern = "^\\+?[1-9]\\d{1,14}$"
        )
        String phoneNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        @Schema(description = "User's email address", example = "jane.smith@example.com", required = true, maxLength = 100)
        String email,

        @NotNull(message = "Role is required")
        @Schema(
                description = "User role (must follow hierarchy rules: Super Admin → Company Admin → Branch Admin → POS User/Employee → Customer)",
                example = "BRANCH_ADMIN",
                required = true,
                allowableValues = {"SUPER_ADMIN", "COMPANY_ADMIN", "BRANCH_ADMIN", "POS_USER", "EMPLOYEE", "CUSTOMER"}
        )
        Role role,

        @Schema(
                description = "Company ID (auto-assigned based on current user context for non-Super Admin users)",
                example = "123e4567-e89b-12d3-a456-426614174000",
                format = "uuid"
        )
        UUID companyId,

        @Schema(
                description = "Branch ID (auto-assigned based on current user context for branch-level users)",
                example = "123e4567-e89b-12d3-a456-426614174001",
                format = "uuid"
        )
        UUID branchId
) {}

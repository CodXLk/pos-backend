package com.codX.pos.auth;

import com.codX.pos.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(
        description = "User registration request for basic user creation",
        example = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "userName": "johndoe",
            "password": "password123",
            "email": "john.doe@example.com",
            "role": "COMPANY_ADMIN"
        }
        """
)
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(
            description = "User's first name",
            example = "John",
            required = true,
            minLength = 2,
            maxLength = 50
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(
            description = "User's last name",
            example = "Doe",
            required = true,
            minLength = 2,
            maxLength = 50
    )
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    @Schema(
            description = "Unique username for login (letters, numbers, and underscores only)",
            example = "johndoe",
            required = true,
            pattern = "^[a-zA-Z0-9_]+$",
            minLength = 3,
            maxLength = 30
    )
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Schema(
            description = "User password (minimum 8 characters)",
            example = "password123",
            required = true,
            minLength = 8,
            maxLength = 100
    )
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "User's email address",
            example = "john.doe@example.com", required = true, maxLength = 100)
    private String email;

    @NotNull(message = "Role is required")
    @Schema(
            description = "User role in the system",
            example = "COMPANY_ADMIN",
            required = true,
            allowableValues = {"SUPER_ADMIN", "COMPANY_ADMIN", "BRANCH_ADMIN", "POS_USER", "EMPLOYEE", "CUSTOMER"}
    )
    private Role role;
}

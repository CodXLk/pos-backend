package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(
        description = "Request to change password using OTP verification",
        example = """
        {
            "phoneNumber": "+94712345678",
            "otpCode": "123456",
            "newPassword": "newSecurePassword123"
        }
        """
)
public record ChangePasswordRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?94\\d{9}$", message = "Invalid phone number format")
        @Schema(
                description = "Phone number associated with the account",
                example = "+94712345678",
                required = true,
                pattern = "^\\+?94\\d{9}$"
        )
        String phoneNumber,

        @NotBlank(message = "OTP code is required")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
        @Schema(
                description = "6-digit OTP code received via SMS",
                example = "123456",
                required = true,
                pattern = "^\\d{6}$",
                minLength = 6,
                maxLength = 6
        )
        String otpCode,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(
                description = "New password (minimum 8 characters)",
                example = "newSecurePassword123",
                required = true,
                minLength = 8,
                maxLength = 100
        )
        String newPassword
) {}

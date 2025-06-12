package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request to reset password with OTP")
public record ResetPasswordRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Schema(description = "Email address", example = "user@aurionx.lk")
        String email,

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        @Schema(description = "6-digit OTP", example = "123456")
        String otp,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(description = "New password", example = "newPassword123")
        String newPassword
) {}

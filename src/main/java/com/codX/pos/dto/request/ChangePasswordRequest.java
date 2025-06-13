package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request to change password for authenticated user")
public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        @Schema(description = "Current password", example = "oldPassword123")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(description = "New password", example = "newSecurePassword123")
        String newPassword
) {}

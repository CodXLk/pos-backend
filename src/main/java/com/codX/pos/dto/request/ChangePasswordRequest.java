package com.codX.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChangePasswordRequest(
        @NotBlank String phoneNumber,
        @NotBlank String otpCode,
        @NotBlank String newPassword
) {}

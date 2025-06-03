package com.codX.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SendOtpRequest(
        @NotBlank String phoneNumber,
        @NotBlank String purpose
) {}

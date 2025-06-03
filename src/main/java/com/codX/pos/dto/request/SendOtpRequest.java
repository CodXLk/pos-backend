package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
@Schema(
        description = "Request to send OTP to phone number",
        example = """
        {
            "phoneNumber": "+1234567890",
            "purpose": "PASSWORD_RESET"
        }
        """
)
public record SendOtpRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
        @Schema(
                description = "Phone number to send OTP to",
                example = "+1234567890",
                required = true,
                pattern = "^\\+?[1-9]\\d{1,14}$"
        )
        String phoneNumber,

        @NotBlank(message = "Purpose is required")
        @Schema(
                description = "Purpose of OTP request",
                example = "PASSWORD_RESET",
                required = true,
                allowableValues = {"PASSWORD_RESET", "PHONE_VERIFICATION", "ACCOUNT_VERIFICATION"}
        )
        String purpose
) {}

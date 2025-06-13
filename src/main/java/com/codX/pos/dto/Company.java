package com.codX.pos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        description = "Company information for business management",
        example = """
        {
            "name": "Tech Solutions Ltd",
            "email": "admin@techsolutions.com",
            "address": "123 Business Street, City",
            "logoUrl": "https://example.com/logo.png",
            "contactNumber": "+94712345678",
            "status": "ACTIVE"
        }
        """
)
public record Company(
        @Schema(description = "Company unique identifier", example = "123e4567-e89b-12d3-a456-426614174000", format = "uuid")
        UUID id,

        @NotBlank(message = "Company name is required")
        @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
        @Schema(
                description = "Company name (must be unique)",
                example = "Tech Solutions Ltd",
                required = true,
                minLength = 2,
                maxLength = 100
        )
        String name,

        @Email(message = "Invalid email format")
        @Schema(
                description = "Company email address",
                example = "admin@techsolutions.com",
                format = "email"
        )
        String email,

        @Size(max = 255, message = "Address cannot exceed 255 characters")
        @Schema(
                description = "Company physical address",
                example = "123 Business Street, City",
                maxLength = 255
        )
        String address,

        @Schema(
                description = "URL to company logo image",
                example = "https://example.com/logo.png",
                format = "uri"
        )
        String logoUrl,

        @Pattern(regexp = "^\\+?94\\d{9}$", message = "Invalid phone number format")
        @Schema(
                description = "Company contact phone number",
                example = "+94712345678",
                pattern = "^\\+?[1-9]\\d{1,14}$"
        )
        String contactNumber,

        @Schema(
                description = "Company status",
                example = "ACTIVE",
                allowableValues = {"ACTIVE", "INACTIVE", "PENDING", "SUSPENDED"}
        )
        Status status,

        @Schema(description = "Company creation timestamp", example = "2023-01-01T10:00:00")
        LocalDateTime createdDate,

        @Schema(description = "Last modification timestamp", example = "2023-01-01T10:00:00")
        LocalDateTime lastModifiedDate,

        @Schema(description = "ID of user who created the company", format = "uuid")
        UUID createdUserId,

        @Schema(description = "ID of user who last modified the company", format = "uuid")
        UUID modifiedUserId
) {}

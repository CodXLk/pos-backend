package com.codX.pos.dto;

import com.codX.pos.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityListeners;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@Schema(
        description = "User data transfer object containing user information and audit details",
        example = """
        {
            "id": "123e4567-e89b-12d3-a456-426614174004",
            "firstName": "John",
            "lastName": "Doe",
            "userName": "johndoe",
            "password": "hashedPassword",
            "role": "COMPANY_ADMIN",
            "createdDate": "2023-01-01T10:00:00",
            "lastModifiedDate": "2023-01-01T10:00:00",
            "createdUserId": "123e4567-e89b-12d3-a456-426614174000",
            "modifiedUserId": "123e4567-e89b-12d3-a456-426614174000"
        }
        """
)
public record User(
        @Schema(
                description = "Unique identifier for the user",
                example = "123e4567-e89b-12d3-a456-426614174004",
                format = "uuid"
        )
        UUID id,

        @Schema(
                description = "User's first name",
                example = "John",
                minLength = 2,
                maxLength = 50
        )
        String firstName,

        @Schema(
                description = "User's last name",
                example = "Doe",
                minLength = 2,
                maxLength = 50
        )
        String lastName,

        @Schema(
                description = "Unique username for login",
                example = "johndoe",
                minLength = 3,
                maxLength = 30
        )
        String userName,

        @Schema(
                description = "Encrypted user password (not returned in API responses)",
                example = "hashedPassword",
                accessMode = Schema.AccessMode.WRITE_ONLY
        )
        String password,

        @Schema(
                description = "User's role in the system",
                example = "COMPANY_ADMIN",
                allowableValues = {"SUPER_ADMIN", "COMPANY_ADMIN", "BRANCH_ADMIN", "POS_USER", "EMPLOYEE", "CUSTOMER"}
        )
        Role role,

        @CreatedDate
        @Schema(
                description = "Timestamp when the user was created",
                example = "2023-01-01T10:00:00",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        LocalDateTime createdDate,

        @LastModifiedDate
        @Schema(
                description = "Timestamp when the user was last modified",
                example = "2023-01-01T10:00:00",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        LocalDateTime lastModifiedDate,

        @Schema(
                description = "ID of the user who created this user",
                example = "123e4567-e89b-12d3-a456-426614174000",
                format = "uuid",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID createdUserId,

        @Schema(
                description = "ID of the user who last modified this user",
                example = "123e4567-e89b-12d3-a456-426614174000",
                format = "uuid",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID modifiedUserId
) {}

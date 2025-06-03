package com.codX.pos.dto.request;

import com.codX.pos.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String userName,
        @NotBlank String phoneNumber,
        @NotNull Role role,
        UUID companyId,
        UUID branchId
) {}

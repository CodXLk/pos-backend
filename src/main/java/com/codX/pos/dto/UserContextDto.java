package com.codX.pos.dto;

import com.codX.pos.entity.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserContextDto(
        UUID userId,
        String username,
        Role role,
        UUID companyId,
        UUID branchId
) {}

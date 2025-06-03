package com.codX.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateBranchRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String contactNumber,
        UUID branchAdminId
) {}

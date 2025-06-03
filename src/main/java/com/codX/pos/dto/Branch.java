package com.codX.pos.dto;

import java.util.UUID;

public record Branch(
        UUID id,
        String name,
        String address,
        String contactNumber,
        UUID companyId,
        UUID branchAdminId,
        boolean isActive
) {}

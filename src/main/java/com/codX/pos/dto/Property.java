package com.codX.pos.dto;

import java.util.UUID;

public record Property(
        UUID id,
        String name,
        String email,
        String contactNumber,
        String address,
        UUID companyId
) {
}

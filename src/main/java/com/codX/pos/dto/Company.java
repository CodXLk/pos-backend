package com.codX.pos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record Company(
        UUID id,
        String name,
        String email,
        String address,
        String logoUrl,
        String contactNumber,
        Status status,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        UUID createdUserId,
        UUID modifiedUserId
) {
}

package com.codX.pos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Status enumeration for entities in the system",
        example = "ACTIVE"
)
public enum Status {
    @Schema(description = "Entity is active and operational")
    ACTIVE,

    @Schema(description = "Entity is inactive and not operational")
    INACTIVE,

    @Schema(description = "Entity is pending approval or activation")
    PENDING,

    @Schema(description = "Entity is suspended temporarily")
    SUSPENDED
}

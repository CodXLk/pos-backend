package com.codX.pos.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Service detail type enumeration")
public enum ServiceDetailType {
    @Schema(description = "Service type entry")
    SERVICE,

    @Schema(description = "Item used in service")
    ITEM
}

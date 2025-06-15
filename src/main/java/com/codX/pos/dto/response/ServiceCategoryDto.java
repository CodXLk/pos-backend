package com.codX.pos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.UUID;

@Builder
@Schema(description = "Service category details")
public record ServiceCategoryDto(
        @Schema(description = "Service category ID")
        UUID id,

        @Schema(description = "Service category name")
        String name,

        @Schema(description = "Service category description")
        String description
) {}

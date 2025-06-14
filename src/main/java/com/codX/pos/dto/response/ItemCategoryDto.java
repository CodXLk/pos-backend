package com.codX.pos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.UUID;

@Builder
@Schema(description = "Item category details")
public record ItemCategoryDto(
        @Schema(description = "Item category ID")
        UUID id,

        @Schema(description = "Item category name")
        String name,

        @Schema(description = "Item category description")
        String description
) {}

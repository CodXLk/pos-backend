package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Request to create a service category")
public record CreateServiceCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Schema(description = "Service category name", example = "Car Wash Services")
        String name,

        @Schema(description = "Category description", example = "All types of car washing services")
        String description
) {}
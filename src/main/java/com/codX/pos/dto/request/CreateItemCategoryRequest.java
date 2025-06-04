package com.codX.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Request to create an item category")
public record CreateItemCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Schema(description = "Item category name", example = "Car Care Products")
        String name,

        @Schema(description = "Category description", example = "All car care and maintenance products")
        String description
) {}
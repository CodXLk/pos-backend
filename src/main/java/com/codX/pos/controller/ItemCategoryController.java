package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateItemCategoryRequest;
import com.codX.pos.entity.ItemCategoryEntity;
import com.codX.pos.service.ItemCategoryService;
import com.codX.pos.util.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/item-categories")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Item Categories", description = "Item category management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Create item category",
            description = "Create a new item category. Only Branch Admin or above can create item categories."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item category created successfully"),
            @ApiResponse(responseCode = "409", description = "Item category name already exists"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> createItemCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Item category creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateItemCategoryRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "name": "Car Care Products",
                            "description": "All car care and maintenance products"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateItemCategoryRequest request) {
        ItemCategoryEntity itemCategory = itemCategoryService.createItemCategory(request);
        return new ResponseEntity<>(
                new StandardResponse(201, itemCategory, "Item category created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get item categories by company",
            description = "Retrieve all item categories for a specific company"
    )
    public ResponseEntity<?> getItemCategoriesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        List<ItemCategoryEntity> categories = itemCategoryService.getItemCategoriesByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, categories, "Item categories retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{companyId}/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get item categories by branch",
            description = "Retrieve all item categories for a specific branch"
    )
    public ResponseEntity<?> getItemCategoriesByBranch(
            @Parameter(description = "Company ID") @PathVariable UUID companyId,
            @Parameter(description = "Branch ID") @PathVariable UUID branchId) {
        List<ItemCategoryEntity> categories = itemCategoryService.getItemCategoriesByBranch(companyId, branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, categories, "Item categories retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Update item category")
    public ResponseEntity<?> updateItemCategory(@PathVariable UUID id, @Valid @RequestBody CreateItemCategoryRequest request) {
        ItemCategoryEntity itemCategory = itemCategoryService.updateItemCategory(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, itemCategory, "Item category updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Deactivate item category")
    public ResponseEntity<?> deactivateItemCategory(@PathVariable UUID id) {
        itemCategoryService.deactivateItemCategory(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Item category deactivated successfully"),
                HttpStatus.OK
        );
    }
}

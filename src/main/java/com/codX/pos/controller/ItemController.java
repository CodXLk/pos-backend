package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateItemRequest;
import com.codX.pos.entity.ItemEntity;
import com.codX.pos.service.ItemService;
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
@RequestMapping("/api/v1/items")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Items", description = "Item management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Create item",
            description = "Create a new item under an item category"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item created successfully"),
            @ApiResponse(responseCode = "409", description = "Item name already exists"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> createItem(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Item creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateItemRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "name": "Engine Oil 5W-30",
                            "description": "Premium synthetic engine oil",
                            "unitPrice": 12.50,
                            "unit": "liters",
                            "stockQuantity": 100,
                            "minStockLevel": 10,
                            "itemCategoryId": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateItemRequest request) {
        ItemEntity item = itemService.createItem(request);
        return new ResponseEntity<>(
                new StandardResponse(201, item, "Item created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get item by ID",
            description = "Retrieve a specific item by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> getItemById(
            @Parameter(description = "Item ID") @PathVariable UUID id) {
        ItemEntity item = itemService.getItemById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, item, "Item retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get items by category",
            description = "Retrieve all items under a specific category"
    )
    public ResponseEntity<?> getItemsByCategory(
            @Parameter(description = "Item Category ID") @PathVariable UUID categoryId) {
        List<ItemEntity> items = itemService.getItemsByCategory(categoryId);
        return new ResponseEntity<>(
                new StandardResponse(200, items, "Items retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Get items by company",
            description = "Retrieve all items for a specific company"
    )
    public ResponseEntity<?> getItemsByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        List<ItemEntity> items = itemService.getItemsByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, items, "Items retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get items by branch",
            description = "Retrieve all items for a specific branch. Access controlled based on user role and hierarchy."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch items retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<?> getItemsByBranch(
            @Parameter(description = "Branch ID") @PathVariable UUID branchId) {
        List<ItemEntity> items = itemService.getItemsByBranch(branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, items, "Branch items retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Update item")
    public ResponseEntity<?> updateItem(@PathVariable UUID id, @Valid @RequestBody CreateItemRequest request) {
        ItemEntity item = itemService.updateItem(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, item, "Item updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Deactivate item")
    public ResponseEntity<?> deactivateItem(@PathVariable UUID id) {
        itemService.deactivateItem(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Item deactivated successfully"),
                HttpStatus.OK
        );
    }
}

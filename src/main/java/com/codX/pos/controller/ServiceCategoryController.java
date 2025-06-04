package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateServiceCategoryRequest;
import com.codX.pos.entity.ServiceCategoryEntity;
import com.codX.pos.service.ServiceCategoryService;
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
@RequestMapping("/api/v1/service-categories")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Service Categories", description = "Service category management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Create service category",
            description = "Create a new service category. Only Branch Admin or above can create service categories."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service category created successfully"),
            @ApiResponse(responseCode = "409", description = "Service category name already exists"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> createServiceCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Service category creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateServiceCategoryRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "name": "Car Wash Services",
                            "description": "All types of car washing services"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateServiceCategoryRequest request) {
        ServiceCategoryEntity serviceCategory = serviceCategoryService.createServiceCategory(request);
        return new ResponseEntity<>(
                new StandardResponse(201, serviceCategory, "Service category created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service categories by company",
            description = "Retrieve all service categories for a specific company"
    )
    public ResponseEntity<?> getServiceCategoriesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        List<ServiceCategoryEntity> categories = serviceCategoryService.getServiceCategoriesByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, categories, "Service categories retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{companyId}/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service categories by branch",
            description = "Retrieve all service categories for a specific branch"
    )
    public ResponseEntity<?> getServiceCategoriesByBranch(
            @Parameter(description = "Company ID") @PathVariable UUID companyId,
            @Parameter(description = "Branch ID") @PathVariable UUID branchId) {
        List<ServiceCategoryEntity> categories = serviceCategoryService.getServiceCategoriesByBranch(companyId, branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, categories, "Service categories retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(summary = "Get service category by ID")
    public ResponseEntity<?> getServiceCategoryById(@PathVariable UUID id) {
        ServiceCategoryEntity serviceCategory = serviceCategoryService.getServiceCategoryById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceCategory, "Service category retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Update service category")
    public ResponseEntity<?> updateServiceCategory(@PathVariable UUID id, @Valid @RequestBody CreateServiceCategoryRequest request) {
        ServiceCategoryEntity serviceCategory = serviceCategoryService.updateServiceCategory(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceCategory, "Service category updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Deactivate service category")
    public ResponseEntity<?> deactivateServiceCategory(@PathVariable UUID id) {
        serviceCategoryService.deactivateServiceCategory(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Service category deactivated successfully"),
                HttpStatus.OK
        );
    }
}

package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateServiceTypeRequest;
import com.codX.pos.dto.request.DiscountRequest;
import com.codX.pos.entity.ServiceTypeEntity;
import com.codX.pos.service.ServiceTypeService;
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
@RequestMapping("/api/v1/service-types")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Service Types", description = "Service type management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Create service type",
            description = "Create a new service type under a service category"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service type created successfully"),
            @ApiResponse(responseCode = "409", description = "Service type name already exists"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> createServiceType(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Service type creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateServiceTypeRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "name": "Full Body Wash",
                            "description": "Complete exterior and interior car wash",
                            "basePrice": 25.00,
                            "estimatedDurationMinutes": 60,
                            "serviceCategoryId": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateServiceTypeRequest request) {
        ServiceTypeEntity serviceType = serviceTypeService.createServiceType(request);
        return new ResponseEntity<>(
                new StandardResponse(201, serviceType, "Service type created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service type by ID",
            description = "Retrieve a specific service type by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service type retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Service type not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> getServiceTypeById(
            @Parameter(description = "Service Type ID") @PathVariable UUID id) {
        ServiceTypeEntity serviceType = serviceTypeService.getServiceTypeById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceType, "Service type retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service types by category",
            description = "Retrieve all service types under a specific category"
    )
    public ResponseEntity<?> getServiceTypesByCategory(
            @Parameter(description = "Service Category ID") @PathVariable UUID categoryId) {
        List<ServiceTypeEntity> serviceTypes = serviceTypeService.getServiceTypesByCategory(categoryId);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceTypes, "Service types retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service types by company",
            description = "Retrieve all service types for a specific company"
    )
    public ResponseEntity<?> getServiceTypesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        List<ServiceTypeEntity> serviceTypes = serviceTypeService.getServiceTypesByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceTypes, "Service types retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service types by branch",
            description = "Retrieve all service types for a specific branch. Access controlled based on user role and hierarchy."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch service types retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<?> getServiceTypesByBranch(
            @Parameter(description = "Branch ID") @PathVariable UUID branchId) {
        List<ServiceTypeEntity> serviceTypes = serviceTypeService.getServiceTypesByBranch(branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceTypes, "Branch service types retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Update service type")
    public ResponseEntity<?> updateServiceType(@PathVariable UUID id, @Valid @RequestBody CreateServiceTypeRequest request) {
        ServiceTypeEntity serviceType = serviceTypeService.updateServiceType(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceType, "Service type updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Deactivate service type")
    public ResponseEntity<?> deactivateServiceType(@PathVariable UUID id) {
        serviceTypeService.deactivateServiceType(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Service type deactivated successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}/default-discount")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Update service type default discount")
    public ResponseEntity<StandardResponse<ServiceTypeEntity>> updateServiceTypeDefaultDiscount(
            @PathVariable UUID id,
            @Valid @RequestBody DiscountRequest discountRequest) {
        ServiceTypeEntity serviceType = serviceTypeService.updateDefaultDiscount(id, discountRequest);
        return new ResponseEntity<>(
                new StandardResponse<>(200, serviceType, "Service type default discount updated successfully"),
                HttpStatus.OK
        );
    }
}

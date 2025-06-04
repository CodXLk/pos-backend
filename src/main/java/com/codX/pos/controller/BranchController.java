package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateBranchRequest;
import com.codX.pos.entity.BranchEntity;
import com.codX.pos.service.BranchService;
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
@RequestMapping("/api/v1/branches") // Note: Updated to match your controller mapping
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Branch Management", description = "Branch management operations with company-level restrictions")
@SecurityRequirement(name = "Bearer Authentication")
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Create a new branch",
            description = """
            Create a new branch for a company. Company Admin can create branches for their company only.
            Super Admin can create branches for any company. The system checks the maximum branch limit
            before creating a new branch.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Branch created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions or access denied"),
            @ApiResponse(responseCode = "400", description = "Maximum branch limit reached or invalid data"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<?> createBranch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Branch creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateBranchRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "name": "Downtown Branch",
                            "address": "789 Downtown Avenue, Business District",
                            "contactNumber": "+1234567892",
                            "branchAdminId": "123e4567-e89b-12d3-a456-426614174003"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateBranchRequest request) {
        BranchEntity branch = branchService.createBranch(request);
        return new ResponseEntity<>(
                new StandardResponse(201, branch, "Branch created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Get branches by company",
            description = "Retrieve all active branches belonging to a specific company. Access is restricted based on user permissions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branches retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied to company branches"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<?> getBranchesByCompany(
            @Parameter(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID companyId) {
        List<BranchEntity> branches = branchService.getBranchesByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, branches, "Branches retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get branch by ID",
            description = "Retrieve a specific branch by its ID. Access is restricted based on organizational hierarchy."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied to branch"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<?> getBranchById(
            @Parameter(description = "Branch ID", example = "123e4567-e89b-12d3-a456-426614174001")
            @PathVariable UUID id) {
        BranchEntity branch = branchService.getBranchById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, branch, "Branch retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Update branch",
            description = "Update branch information. Only Super Admin and Company Admin can update branches."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Branch not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> updateBranch(
            @Parameter(description = "Branch ID", example = "123e4567-e89b-12d3-a456-426614174001")
            @PathVariable UUID id,
            @Valid @RequestBody CreateBranchRequest request) {
        BranchEntity branch = branchService.updateBranch(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, branch, "Branch updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Deactivate branch",
            description = "Deactivate a branch (soft delete). This will affect all users associated with the branch."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<?> deactivateBranch(
            @Parameter(description = "Branch ID", example = "123e4567-e89b-12d3-a456-426614174001")
            @PathVariable UUID id) {
        branchService.deactivateBranch(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Branch deactivated successfully"),
                HttpStatus.OK
        );
    }
}

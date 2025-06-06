package com.codX.pos.controller;

import com.codX.pos.dto.Company;
import com.codX.pos.entity.CompanyEntity;
import com.codX.pos.service.CompanyService;
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
@RequestMapping("/api/v1/company")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Company Management", description = "Company management operations (Super Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Create a new company",
            description = "Create a new company with default settings. Only Super Admin can perform this operation. Each company gets a default maximum branch limit of 5."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Company created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Only Super Admin can create companies"),
            @ApiResponse(responseCode = "409", description = "Company name already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> create(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Company creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Company.class),
                            examples = @ExampleObject(value = """
                        {
                            "name": "Tech Solutions Ltd",
                            "email": "admin@techsolutions.com",
                            "address": "123 Business Street, City",
                            "logoUrl": "https://example.com/logo.png",
                            "contactNumber": "+1234567890",
                            "status": "ACTIVE"
                        }
                        """)
                    )
            )
            @RequestBody Company company) {
        CompanyEntity createdCompany = companyService.create(company);
        return new ResponseEntity<>(
                new StandardResponse(201, createdCompany, "Company Created Successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get all companies",
            description = "Retrieve all active companies in the system. Only Super Admin can access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Only Super Admin can view all companies")
    })
    public ResponseEntity<?> getAllCompanies() {
        List<CompanyEntity> companies = companyService.getAllActiveCompanies();
        return new ResponseEntity<>(
                new StandardResponse(200, companies, "Companies retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Get company by ID",
            description = "Retrieve a specific company by ID. Super Admin can access any company, Company Admin can only access their own company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied to company data"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<?> getCompanyById(
            @Parameter(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        CompanyEntity company = companyService.getCompanyById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, company, "Company retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Update company",
            description = "Update company information. Super Admin can update any company, Company Admin can only update their own company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "409", description = "Company name already exists")
    })
    public ResponseEntity<?> updateCompany(
            @Parameter(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Valid @RequestBody Company company) {
        CompanyEntity updatedCompany = companyService.updateCompany(id, company);
        return new ResponseEntity<>(
                new StandardResponse(200, updatedCompany, "Company updated successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}/max-branches")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Update maximum branches limit",
            description = "Update the maximum number of branches allowed for a company. Only Super Admin can modify branch limits."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch limit updated successfully"),
            @ApiResponse(responseCode = "403", description = "Only Super Admin can update branch limits"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "400", description = "Invalid branch limit value")
    })
    public ResponseEntity<?> updateMaxBranches(
            @Parameter(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Parameter(description = "Maximum branches allowed", example = "10")
            @RequestParam int maxBranches) {
        CompanyEntity company = companyService.updateMaxBranches(id, maxBranches);
        return new ResponseEntity<>(
                new StandardResponse(200, company, "Branch limit updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Deactivate company",
            description = "Deactivate a company (soft delete). This will also affect all branches and users associated with the company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Only Super Admin can deactivate companies"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<?> deactivateCompany(
            @Parameter(description = "Company ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        companyService.deactivateCompany(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Company deactivated successfully"),
                HttpStatus.OK
        );
    }
}

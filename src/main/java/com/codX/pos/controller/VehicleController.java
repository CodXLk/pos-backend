package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateVehicleRequest;
import com.codX.pos.dto.response.VehicleResponse;
import com.codX.pos.entity.VehicleEntity;
import com.codX.pos.service.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Vehicle Management", description = "Vehicle management operations for service station")
@SecurityRequirement(name = "Bearer Authentication")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Create a new vehicle",
            description = "Register a new vehicle for a customer. Vehicle number must be unique within the company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "409", description = "Vehicle number already exists"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> createVehicle(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Vehicle creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateVehicleRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "vehicleNumber": "ABC-1234",
                            "make": "Toyota",
                            "model": "Corolla",
                            "year": 2020,
                            "color": "White",
                            "engineNumber": "ENG123456",
                            "chassisNumber": "CHS789012",
                            "customerId": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateVehicleRequest request) {
        VehicleEntity vehicle = vehicleService.createVehicle(request);
        return new ResponseEntity<>(
                new StandardResponse(201, vehicle, "Vehicle created successfully"),
                HttpStatus.CREATED
        );
    }

    // NEW ENDPOINT: Get all vehicles
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get all vehicles (Super Admin only)",
            description = "Retrieve all vehicles across all companies. Only accessible by Super Admin."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All vehicles retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions - Super Admin access required")
    })
    public ResponseEntity<?> getAllVehicles() {
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles();
        return new ResponseEntity<>(
                new StandardResponse(200, vehicles, "All vehicles retrieved successfully"),
                HttpStatus.OK
        );
    }

    // NEW ENDPOINT: Get vehicles by company
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    @Operation(
            summary = "Get vehicles by company",
            description = "Retrieve all vehicles for a specific company. Super Admin can access any company, Company Admin can only access their own company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company vehicles retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<?> getVehiclesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, vehicles, "Company vehicles retrieved successfully"),
                HttpStatus.OK
        );
    }

    // NEW ENDPOINT: Get vehicles by branch
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get vehicles by branch",
            description = "Retrieve all vehicles for a specific branch. Access controlled based on user role and hierarchy."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch vehicles retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<?> getVehiclesByBranch(
            @Parameter(description = "Branch ID") @PathVariable UUID branchId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByBranch(branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, vehicles, "Branch vehicles retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get vehicles by customer",
            description = "Retrieve all vehicles registered under a specific customer"
    )
    public ResponseEntity<?> getVehiclesByCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID customerId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByCustomer(customerId);
        return new ResponseEntity<>(
                new StandardResponse(200, vehicles, "Vehicles retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Search vehicles by number",
            description = "Search vehicles by vehicle number (partial match)"
    )
    public ResponseEntity<?> searchVehicles(
            @Parameter(description = "Vehicle number to search") @RequestParam String vehicleNumber) {
        List<VehicleResponse> vehicles = vehicleService.searchVehiclesByNumber(vehicleNumber);
        return new ResponseEntity<>(
                new StandardResponse(200, vehicles, "Vehicles found"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<?> getVehicleById(@PathVariable UUID id) {
        VehicleResponse vehicle = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, vehicle, "Vehicle retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(summary = "Update vehicle information")
    public ResponseEntity<?> updateVehicle(@PathVariable UUID id, @Valid @RequestBody CreateVehicleRequest request) {
        VehicleEntity vehicle = vehicleService.updateVehicle(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, vehicle, "Vehicle updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Deactivate vehicle")
    public ResponseEntity<?> deactivateVehicle(@PathVariable UUID id) {
        vehicleService.deactivateVehicle(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Vehicle deactivated successfully"),
                HttpStatus.OK
        );
    }
}

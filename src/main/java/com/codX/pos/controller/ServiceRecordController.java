package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.dto.response.ServiceRecordResponse;
import com.codX.pos.entity.ServiceRecordEntity;
import com.codX.pos.service.ServiceRecordService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service-records")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Service Records", description = "Service record management for vehicles")
@SecurityRequirement(name = "Bearer Authentication")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Create service record",
            description = "Create a new service record for a vehicle with services and items used"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid service data or insufficient stock"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<StandardResponse<ServiceRecordEntity>> createServiceRecord(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Service record creation details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateServiceRecordRequest.class),
                            examples = @ExampleObject(value = """
                            {
                                "vehicleId": "123e4567-e89b-12d3-a456-426614174001",
                                "customerId": "123e4567-e89b-12d3-a456-426614174002",
                                "serviceDate": "2024-01-15T10:30:00",
                                "currentMileage": 50000,
                                "notes": "Regular maintenance service",
                                "status": "PENDING",
                                "serviceDetails": [
                                    {
                                        "serviceTypeId": "123e4567-e89b-12d3-a456-426614174003",
                                        "quantity": 1,
                                        "notes": "Full body wash service",
                                        "items": [
                                            {
                                                "itemId": "123e4567-e89b-12d3-a456-426614174004",
                                                "quantity": 2,
                                                "notes": "Car shampoo used"
                                            }
                                        ]
                                    }
                                ]
                            }
                            """)
                    )
            )
            @Valid @RequestBody CreateServiceRecordRequest request) {
        ServiceRecordEntity serviceRecord = serviceRecordService.createServiceRecord(request);
        return new ResponseEntity<>(
                new StandardResponse<>(201, serviceRecord, "Service record created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER', 'CUSTOMER')")
    @Operation(
            summary = "Get service history by vehicle",
            description = "Retrieve complete service history for a specific vehicle with service details"
    )
    public ResponseEntity<StandardResponse<List<ServiceRecordResponse>>> getServiceRecordsByVehicle(
            @Parameter(description = "Vehicle ID") @PathVariable UUID vehicleId) {
        List<ServiceRecordResponse> serviceRecords = serviceRecordService.getServiceRecordsByVehicle(vehicleId);
        return new ResponseEntity<>(
                new StandardResponse<>(200, serviceRecords, "Service records retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service records by customer",
            description = "Retrieve all service records for a specific customer across all their vehicles with service details"
    )
    public ResponseEntity<StandardResponse<List<ServiceRecordResponse>>> getServiceRecordsByCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID customerId) {
        List<ServiceRecordResponse> serviceRecords = serviceRecordService.getServiceRecordsByCustomer(customerId);
        return new ResponseEntity<>(
                new StandardResponse<>(200, serviceRecords, "Service records retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service records by branch",
            description = "Retrieve all service records for a specific branch with service details"
    )
    public ResponseEntity<StandardResponse<List<ServiceRecordResponse>>> getServiceRecordsByBranch(
            @Parameter(description = "Branch ID") @PathVariable UUID branchId) {
        List<ServiceRecordResponse> serviceRecords = serviceRecordService.getServiceRecordsByBranch(branchId);
        return new ResponseEntity<>(
                new StandardResponse<>(200, serviceRecords, "Branch service records retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get service records by date range",
            description = "Retrieve service records within a specific date range for reporting with service details"
    )
    public ResponseEntity<StandardResponse<List<ServiceRecordResponse>>> getServiceRecordsByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ServiceRecordResponse> serviceRecords = serviceRecordService.getServiceRecordsByDateRange(startDate, endDate);
        return new ResponseEntity<>(
                new StandardResponse<>(200, serviceRecords, "Service records retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(summary = "Get service record by ID with service details")
    public ResponseEntity<StandardResponse<ServiceRecordResponse>> getServiceRecordById(@PathVariable UUID id) {
        ServiceRecordResponse serviceRecord = serviceRecordService.getServiceRecordById(id);
        return new ResponseEntity<>(
                new StandardResponse<>(200, serviceRecord, "Service record retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(summary = "Update service record with service details")
    public ResponseEntity<StandardResponse<ServiceRecordEntity>> updateServiceRecord(@PathVariable UUID id, @Valid @RequestBody CreateServiceRecordRequest request) {
        ServiceRecordEntity serviceRecord = serviceRecordService.updateServiceRecord(id, request);
        return new ResponseEntity<>(
                new StandardResponse<>(200, serviceRecord, "Service record updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Delete service record")
    public ResponseEntity<StandardResponse<Void>> deleteServiceRecord(@PathVariable UUID id) {
        serviceRecordService.deleteServiceRecord(id);
        return new ResponseEntity<>(
                new StandardResponse<>(200, null, "Service record deleted successfully"),
                HttpStatus.OK
        );
    }
}

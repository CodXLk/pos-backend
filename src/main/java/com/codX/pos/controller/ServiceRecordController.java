package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.dto.response.ServiceRecordResponse;
import com.codX.pos.entity.ServiceRecordEntity;
import com.codX.pos.service.ServiceRecordService;
import com.codX.pos.util.StandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public ResponseEntity<?> createServiceRecord(@Valid @RequestBody CreateServiceRecordRequest request) {
        ServiceRecordEntity serviceRecord = serviceRecordService.createServiceRecord(request);
        return new ResponseEntity<>(
                new StandardResponse(201, serviceRecord, "Service record created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER', 'CUSTOMER')")
    @Operation(
            summary = "Get service history by vehicle",
            description = "Retrieve complete service history for a specific vehicle with service details"
    )
    public ResponseEntity<?> getServiceRecordsByVehicle(
            @Parameter(description = "Vehicle ID") @PathVariable UUID vehicleId) {
        List<ServiceRecordResponse> serviceRecords = serviceRecordService.getServiceRecordsByVehicle(vehicleId);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceRecords, "Service records retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get service records by customer",
            description = "Retrieve all service records for a specific customer across all their vehicles with service details"
    )
    public ResponseEntity<?> getServiceRecordsByCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID customerId) {
        List<ServiceRecordResponse> serviceRecords = serviceRecordService.getServiceRecordsByCustomer(customerId);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceRecords, "Service records retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get service records by date range",
            description = "Retrieve service records within a specific date range for reporting with service details"
    )
    public ResponseEntity<?> getServiceRecordsByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ServiceRecordResponse> serviceRecords = serviceRecordService.getServiceRecordsByDateRange(startDate, endDate);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceRecords, "Service records retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(summary = "Get service record by ID with service details")
    public ResponseEntity<?> getServiceRecordById(@PathVariable UUID id) {
        ServiceRecordResponse serviceRecord = serviceRecordService.getServiceRecordById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceRecord, "Service record retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(summary = "Update service record with service details")
    public ResponseEntity<?> updateServiceRecord(@PathVariable UUID id, @Valid @RequestBody CreateServiceRecordRequest request) {
        ServiceRecordEntity serviceRecord = serviceRecordService.updateServiceRecord(id, request);
        return new ResponseEntity<>(
                new StandardResponse(200, serviceRecord, "Service record updated successfully"),
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
}

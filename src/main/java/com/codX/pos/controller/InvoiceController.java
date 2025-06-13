package com.codX.pos.controller;

import com.codX.pos.dto.request.CreateInvoiceRequest;
import com.codX.pos.dto.response.InvoiceResponse;
import com.codX.pos.entity.InvoiceEntity;
import com.codX.pos.entity.InvoiceStatus;
import com.codX.pos.service.InvoiceService;
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
@RequestMapping("/api/v1/invoices")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Invoice Management", description = "Invoice management operations for services and item sales")
@SecurityRequirement(name = "Bearer Authentication")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/service/{serviceRecordId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Create service invoice",
            description = "Create an invoice for a completed service record"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service invoice created successfully"),
            @ApiResponse(responseCode = "409", description = "Invoice already exists for this service record"),
            @ApiResponse(responseCode = "404", description = "Service record not found")
    })
    public ResponseEntity<?> createServiceInvoice(
            @Parameter(description = "Service Record ID") @PathVariable UUID serviceRecordId) {
        InvoiceEntity invoice = invoiceService.createServiceInvoice(serviceRecordId);
        return new ResponseEntity<>(
                new StandardResponse(201, invoice, "Service invoice created successfully"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/item-sale")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Create item sale invoice",
            description = "Create an invoice for item sales (without service)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item sale invoice created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid item data or insufficient stock")
    })
    public ResponseEntity<?> createItemSaleInvoice(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Item sale invoice details",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateInvoiceRequest.class),
                            examples = @ExampleObject(value = """
                        {
                            "customerId": "123e4567-e89b-12d3-a456-426614174000",
                            "type": "ITEM_SALE",
                            "discountAmount": 5.00,
                            "taxPercentage": 10.0,
                            "items": [
                                {
                                    "itemId": "123e4567-e89b-12d3-a456-426614174001",
                                    "quantity": 2,
                                    "type": "ITEM"
                                }
                            ]
                        }
                        """)
                    )
            )
            @Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceEntity invoice = invoiceService.createItemSaleInvoice(request);
        return new ResponseEntity<>(
                new StandardResponse(201, invoice, "Item sale invoice created successfully"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/mixed")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Create mixed invoice",
            description = "Create an invoice for both services and item sales"
    )
    public ResponseEntity<?> createMixedInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceEntity invoice = invoiceService.createMixedInvoice(request);
        return new ResponseEntity<>(
                new StandardResponse(201, invoice, "Mixed invoice created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER', 'CUSTOMER')")
    @Operation(
            summary = "Get invoice by ID",
            description = "Retrieve invoice details by ID with all invoice items"
    )
    public ResponseEntity<?> getInvoiceById(@PathVariable UUID id) {
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, invoice, "Invoice retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/number/{invoiceNumber}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER', 'CUSTOMER')")
    @Operation(
            summary = "Get invoice by invoice number",
            description = "Retrieve invoice details by invoice number with all invoice items"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<?> getInvoiceByNumber(
            @Parameter(description = "Invoice Number", example = "INV-20241215-0001") @PathVariable String invoiceNumber) {
        InvoiceResponse invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return new ResponseEntity<>(
                new StandardResponse(200, invoice, "Invoice retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get invoices by customer",
            description = "Retrieve all invoices for a specific customer"
    )
    public ResponseEntity<?> getInvoicesByCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID customerId) {
        List<InvoiceResponse> invoices = invoiceService.getInvoicesByCustomer(customerId);
        return new ResponseEntity<>(
                new StandardResponse(200, invoices, "Customer invoices retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get invoices by company",
            description = "Retrieve all invoices for a specific company"
    )
    public ResponseEntity<?> getInvoicesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        List<InvoiceResponse> invoices = invoiceService.getInvoicesByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, invoices, "Company invoices retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Get invoices by branch",
            description = "Retrieve all invoices for a specific branch. Access controlled based on user role and hierarchy."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch invoices retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<?> getInvoicesByBranch(
            @Parameter(description = "Branch ID") @PathVariable UUID branchId) {
        List<InvoiceResponse> invoices = invoiceService.getInvoicesByBranch(branchId);
        return new ResponseEntity<>(
                new StandardResponse(200, invoices, "Branch invoices retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN')")
    @Operation(
            summary = "Get invoices by date range",
            description = "Retrieve invoices within a specific date range for reporting"
    )
    public ResponseEntity<?> getInvoicesByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<InvoiceResponse> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);
        return new ResponseEntity<>(
                new StandardResponse(200, invoices, "Invoices retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Update invoice status",
            description = "Update the status of an invoice (DRAFT, SENT, PAID, CANCELLED, OVERDUE)"
    )
    public ResponseEntity<?> updateInvoiceStatus(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Parameter(description = "New invoice status") @RequestParam InvoiceStatus status) {
        invoiceService.updateInvoiceStatus(id, status);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Invoice status updated successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/generate-number")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'BRANCH_ADMIN', 'POS_USER')")
    @Operation(
            summary = "Generate invoice number",
            description = "Generate a new invoice number for preview purposes"
    )
    public ResponseEntity<?> generateInvoiceNumber() {
        String invoiceNumber = invoiceService.generateInvoiceNumber();
        return new ResponseEntity<>(
                new StandardResponse(200, invoiceNumber, "Invoice number generated successfully"),
                HttpStatus.OK
        );
    }
}

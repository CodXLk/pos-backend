package com.codX.pos.service;

import com.codX.pos.dto.request.CreateInvoiceRequest;
import com.codX.pos.dto.request.UpdateInvoiceDiscountRequest;
import com.codX.pos.dto.response.InvoiceResponse;
import com.codX.pos.dto.response.InvoicePreviewResponse;
import com.codX.pos.entity.InvoiceEntity;
import com.codX.pos.entity.InvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    InvoiceEntity createServiceInvoice(UUID serviceRecordId);
    InvoiceEntity createItemSaleInvoice(CreateInvoiceRequest request);
    InvoiceEntity createMixedInvoice(CreateInvoiceRequest request);
    InvoiceResponse getInvoiceById(UUID id);
    InvoiceResponse getInvoiceByNumber(String invoiceNumber);
    List<InvoiceResponse> getInvoicesByCustomer(UUID customerId);
    List<InvoiceResponse> getInvoicesByCompany(UUID companyId);
    List<InvoiceResponse> getInvoicesByBranch(UUID branchId);
    List<InvoiceResponse> getInvoicesByBranchWithDefaults(UUID branchId); // NEW
    List<InvoiceResponse> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    void updateInvoiceStatus(UUID id, InvoiceStatus status);
    InvoiceResponse updateInvoiceDiscounts(UUID id, UpdateInvoiceDiscountRequest request); // NEW
    InvoicePreviewResponse previewInvoiceWithDiscounts(CreateInvoiceRequest request); // NEW
    String generateInvoiceNumber();
}

package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateInvoiceRequest;
import com.codX.pos.dto.response.InvoiceItemResponse;
import com.codX.pos.dto.response.InvoiceResponse;
import com.codX.pos.entity.*;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.*;
import com.codX.pos.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ServiceRecordRepository serviceRecordRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public InvoiceEntity createServiceInvoice(UUID serviceRecordId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.POS_USER && currentUser.role() != Role.BRANCH_ADMIN &&
                currentUser.role() != Role.COMPANY_ADMIN && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Insufficient permissions to create invoice");
        }

        // Get service record
        ServiceRecordEntity serviceRecord = serviceRecordRepository.findByIdAndCompanyId(serviceRecordId, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        // Check if invoice already exists for this service record
        if (invoiceRepository.findByServiceRecordIdAndCompanyId(serviceRecordId, currentUser.companyId()).isPresent()) {
            throw new RuntimeException("Invoice already exists for this service record");
        }

        // Create invoice
        InvoiceEntity invoice = InvoiceEntity.builder()
                .invoiceNumber(generateInvoiceNumber())
                .invoiceDate(LocalDateTime.now())
                .customerId(serviceRecord.getCustomerId())
                .vehicleId(serviceRecord.getVehicleId())
                .serviceRecordId(serviceRecordId)
                .type(InvoiceType.SERVICE)
                .status(InvoiceStatus.DRAFT)
                .subtotal(serviceRecord.getTotalAmount())
                .taxAmount(serviceRecord.getTotalAmount().multiply(new BigDecimal("0.10"))) // 10% tax
                .discountAmount(BigDecimal.ZERO)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        // Calculate total
        BigDecimal total = invoice.getSubtotal()
                .add(invoice.getTaxAmount())
                .subtract(invoice.getDiscountAmount());
        invoice.setTotalAmount(total);

        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);

        // Update service record with invoice ID
        serviceRecord.setInvoiceId(savedInvoice.getId());
        serviceRecordRepository.save(serviceRecord);

        return savedInvoice;
    }

    @Override
    @Transactional
    public InvoiceEntity createItemSaleInvoice(CreateInvoiceRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        // Create invoice
        InvoiceEntity invoice = InvoiceEntity.builder()
                .invoiceNumber(generateInvoiceNumber())
                .invoiceDate(LocalDateTime.now())
                .customerId(request.customerId())
                .vehicleId(request.vehicleId())
                .type(InvoiceType.ITEM_SALE)
                .status(InvoiceStatus.DRAFT)
                .discountAmount(request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);

        // Create invoice items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (var itemRequest : request.items()) {
            ItemEntity item = itemRepository.findByIdAndCompanyIdAndIsActiveTrue(itemRequest.itemId(), currentUser.companyId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemRequest.itemId()));

            BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(itemRequest.quantity()));
            subtotal = subtotal.add(itemTotal);

            InvoiceItemEntity invoiceItem = InvoiceItemEntity.builder()
                    .invoiceId(savedInvoice.getId())
                    .itemId(itemRequest.itemId())
                    .description(item.getName())
                    .quantity(itemRequest.quantity())
                    .unitPrice(item.getUnitPrice())
                    .totalPrice(itemTotal)
                    .type(InvoiceItemType.ITEM)
                    .companyId(currentUser.companyId())
                    .branchId(currentUser.branchId())
                    .build();

            invoiceItemRepository.save(invoiceItem);

            // Update item stock
            item.setStockQuantity(item.getStockQuantity() - itemRequest.quantity());
            itemRepository.save(item);
        }

        // Calculate totals
        BigDecimal taxPercentage = request.taxPercentage() != null ? request.taxPercentage() : new BigDecimal("10.0");
        BigDecimal taxAmount = subtotal.multiply(taxPercentage.divide(new BigDecimal("100")));
        BigDecimal total = subtotal.add(taxAmount).subtract(invoice.getDiscountAmount());

        savedInvoice.setSubtotal(subtotal);
        savedInvoice.setTaxAmount(taxAmount);
        savedInvoice.setTotalAmount(total);

        return invoiceRepository.save(savedInvoice);
    }

    @Override
    @Transactional
    public InvoiceEntity createMixedInvoice(CreateInvoiceRequest request) {
        // Implementation for mixed invoices
        return createItemSaleInvoice(request); // Simplified for now
    }

    @Override
    public InvoiceResponse getInvoiceById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        InvoiceEntity invoice = invoiceRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        List<InvoiceItemEntity> invoiceItems = invoiceItemRepository.findByInvoiceIdAndCompanyId(id, currentUser.companyId());

        return mapToResponse(invoice, invoiceItems);
    }

    @Override
    public List<InvoiceResponse> getInvoicesByCustomer(UUID customerId) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<InvoiceEntity> invoices = invoiceRepository.findByCustomerIdAndCompanyIdOrderByInvoiceDateDesc(customerId, currentUser.companyId());

        return invoices.stream()
                .map(invoice -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceIdAndCompanyId(invoice.getId(), currentUser.companyId());
                    return mapToResponse(invoice, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company invoices");
        }

        List<InvoiceEntity> invoices = invoiceRepository.findByCompanyIdAndBranchIdOrderByInvoiceDateDesc(companyId, currentUser.branchId());

        return invoices.stream()
                .map(invoice -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceIdAndCompanyId(invoice.getId(), companyId);
                    return mapToResponse(invoice, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<InvoiceEntity> invoices = invoiceRepository.findByDateRangeAndCompanyId(startDate, endDate, currentUser.companyId());

        return invoices.stream()
                .map(invoice -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceIdAndCompanyId(invoice.getId(), currentUser.companyId());
                    return mapToResponse(invoice, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInvoiceStatus(UUID id, InvoiceStatus status) {
        UserContextDto currentUser = UserContext.getUserContext();

        InvoiceEntity invoice = invoiceRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(status);
        invoiceRepository.save(invoice);
    }

    @Override
    public String generateInvoiceNumber() {
        UserContextDto currentUser = UserContext.getUserContext();

        // Generate invoice number format: INV-YYYYMMDD-XXXX
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Get count of invoices for today to generate sequence
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<InvoiceEntity> todayInvoices = invoiceRepository.findByDateRangeAndCompanyId(startOfDay, endOfDay, currentUser.companyId());
        int sequence = todayInvoices.size() + 1;

        return String.format("INV-%s-%04d", dateStr, sequence);
    }

    private InvoiceResponse mapToResponse(InvoiceEntity invoice, List<InvoiceItemEntity> invoiceItems) {
        List<InvoiceItemResponse> itemResponses = invoiceItems.stream()
                .map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .type(item.getType())
                        .build())
                .collect(Collectors.toList());

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .discountAmount(invoice.getDiscountAmount())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .type(invoice.getType())
                .customerId(invoice.getCustomerId())
                .vehicleId(invoice.getVehicleId())
                .serviceRecordId(invoice.getServiceRecordId())
                .items(itemResponses)
                .build();
    }
}

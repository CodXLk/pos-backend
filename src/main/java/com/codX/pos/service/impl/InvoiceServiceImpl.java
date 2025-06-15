package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateInvoiceRequest;
import com.codX.pos.dto.request.UpdateInvoiceDiscountRequest;
import com.codX.pos.dto.response.InvoiceItemResponse;
import com.codX.pos.dto.response.InvoiceResponse;
import com.codX.pos.dto.response.InvoicePreviewResponse;
import com.codX.pos.entity.*;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.*;
import com.codX.pos.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final ServiceTypeRepository serviceTypeRepository;

    @Override
    @Transactional
    public InvoiceEntity createServiceInvoice(UUID serviceRecordId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.POS_USER && currentUser.role() != Role.BRANCH_ADMIN &&
                currentUser.role() != Role.COMPANY_ADMIN && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Insufficient permissions to create invoice");
        }

        ServiceRecordEntity serviceRecord = serviceRecordRepository.findByIdAndCompanyId(serviceRecordId, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        if (invoiceRepository.findByServiceRecordIdAndCompanyId(serviceRecordId, currentUser.companyId()).isPresent()) {
            throw new RuntimeException("Invoice already exists for this service record");
        }

        InvoiceEntity invoice = InvoiceEntity.builder()
                .invoiceNumber(generateInvoiceNumber())
                .invoiceDate(LocalDateTime.now())
                .customerId(serviceRecord.getCustomerId())
                .vehicleId(serviceRecord.getVehicleId())
                .serviceRecordId(serviceRecordId)
                .type(InvoiceType.SERVICE)
                .status(InvoiceStatus.DRAFT)
                .subtotal(serviceRecord.getTotalAmount())
                .taxAmount(serviceRecord.getTotalAmount().multiply(new BigDecimal("0.10")))
                .discountAmount(BigDecimal.ZERO)
                .overallDiscountValue(BigDecimal.ZERO)
                .overallDiscountType(DiscountType.PERCENTAGE)
                .overallDiscountAmount(BigDecimal.ZERO)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        BigDecimal total = invoice.getSubtotal()
                .add(invoice.getTaxAmount())
                .subtract(invoice.getDiscountAmount());
        invoice.setTotalAmount(total);

        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);

        serviceRecord.setInvoiceId(savedInvoice.getId());
        serviceRecordRepository.save(serviceRecord);

        return savedInvoice;
    }

    @Override
    @Transactional
    public InvoiceEntity createItemSaleInvoice(CreateInvoiceRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        InvoiceEntity invoice = InvoiceEntity.builder()
                .invoiceNumber(generateInvoiceNumber())
                .invoiceDate(LocalDateTime.now())
                .customerId(request.customerId())
                .vehicleId(request.vehicleId())
                .type(InvoiceType.ITEM_SALE)
                .status(InvoiceStatus.DRAFT)
                .discountAmount(request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO)
                .overallDiscountValue(BigDecimal.ZERO)
                .overallDiscountType(DiscountType.PERCENTAGE)
                .overallDiscountAmount(BigDecimal.ZERO)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (var itemRequest : request.items()) {
            ItemEntity item = itemRepository.findByIdAndCompanyIdAndIsActiveTrue(itemRequest.itemId(), currentUser.companyId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemRequest.itemId()));

            BigDecimal unitPrice = itemRequest.unitPrice() != null ? itemRequest.unitPrice() : item.getUnitPrice();
            BigDecimal itemTotal = unitPrice.multiply(new BigDecimal(itemRequest.quantity()));

            // Apply default discount
            BigDecimal discountAmount = calculateDiscountAmount(itemTotal, item.getDefaultDiscountValue(), item.getDefaultDiscountType());
            BigDecimal finalPrice = itemTotal.subtract(discountAmount);
            subtotal = subtotal.add(finalPrice);

            InvoiceItemEntity invoiceItem = InvoiceItemEntity.builder()
                    .invoiceId(savedInvoice.getId())
                    .itemId(itemRequest.itemId())
                    .description(item.getName())
                    .quantity(itemRequest.quantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .discountValue(item.getDefaultDiscountValue())
                    .discountType(item.getDefaultDiscountType())
                    .discountAmount(discountAmount)
                    .finalPrice(finalPrice)
                    .type(InvoiceItemType.ITEM)
                    .companyId(currentUser.companyId())
                    .branchId(currentUser.branchId())
                    .build();

            invoiceItemRepository.save(invoiceItem);

            item.setStockQuantity(item.getStockQuantity() - itemRequest.quantity());
            itemRepository.save(item);
        }

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
        return createItemSaleInvoice(request);
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
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        UserContextDto currentUser = UserContext.getUserContext();

        InvoiceEntity invoice = invoiceRepository.findByInvoiceNumberAndCompanyId(invoiceNumber, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Invoice not found with number: " + invoiceNumber));

        List<InvoiceItemEntity> invoiceItems = invoiceItemRepository.findByInvoiceIdAndCompanyId(invoice.getId(), currentUser.companyId());

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

        // Use the correct method name
        List<InvoiceEntity> invoices = invoiceRepository.findByCompanyIdOrderByInvoiceDateDesc(companyId);

        return invoices.stream()
                .map(invoice -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceIdAndCompanyId(invoice.getId(), companyId);
                    return mapToResponse(invoice, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByBranch(UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();

        switch (currentUser.role()) {
            case SUPER_ADMIN:
                break;
            case COMPANY_ADMIN:
                break;
            case BRANCH_ADMIN:
            case POS_USER:
                if (!currentUser.branchId().equals(branchId)) {
                    throw new UnauthorizedException("You can only access invoices from your own branch");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to access branch invoices");
        }

        List<InvoiceEntity> invoices = invoiceRepository.findByBranchIdOrderByInvoiceDateDesc(branchId);

        return invoices.stream()
                .map(invoice -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceIdAndCompanyId(invoice.getId(), currentUser.companyId());
                    return mapToResponse(invoice, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByBranchWithDefaults(UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();

        switch (currentUser.role()) {
            case SUPER_ADMIN:
                break;
            case COMPANY_ADMIN:
                break;
            case BRANCH_ADMIN:
            case POS_USER:
                if (!currentUser.branchId().equals(branchId)) {
                    throw new UnauthorizedException("You can only access invoices from your own branch");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to access branch invoices");
        }

        List<InvoiceEntity> invoices = invoiceRepository.findByBranchIdOrderByInvoiceDateDesc(branchId);

        return invoices.stream()
                .map(invoice -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceIdAndCompanyId(
                            invoice.getId(), currentUser.companyId());
                    return mapToResponseWithDefaults(invoice, items);
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
    @Transactional
    public InvoiceResponse updateInvoiceDiscounts(UUID id, UpdateInvoiceDiscountRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        InvoiceEntity invoice = invoiceRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new RuntimeException("Cannot modify paid or cancelled invoice");
        }

        if (request.invoiceDiscount() != null) {
            invoice.setOverallDiscountType(request.invoiceDiscount().type());
            invoice.setOverallDiscountValue(request.invoiceDiscount().value());
        }

        List<InvoiceItemEntity> invoiceItems = invoiceItemRepository.findByInvoiceIdAndCompanyId(id, currentUser.companyId());

        if (request.itemDiscounts() != null) {
            request.itemDiscounts().forEach(itemDiscount -> {
                invoiceItems.stream()
                        .filter(item -> item.getItemId() != null && item.getItemId().equals(itemDiscount.itemId()))
                        .forEach(item -> {
                            item.setDiscountType(itemDiscount.discount().type());
                            item.setDiscountValue(itemDiscount.discount().value());
                            updateItemDiscountCalculations(item);
                        });
            });
        }

        if (request.serviceDiscounts() != null) {
            request.serviceDiscounts().forEach(serviceDiscount -> {
                invoiceItems.stream()
                        .filter(item -> item.getServiceTypeId() != null &&
                                item.getServiceTypeId().equals(serviceDiscount.serviceTypeId()))
                        .forEach(item -> {
                            item.setDiscountType(serviceDiscount.discount().type());
                            item.setDiscountValue(serviceDiscount.discount().value());
                            updateItemDiscountCalculations(item);
                        });
            });
        }

        invoiceItemRepository.saveAll(invoiceItems);
        recalculateInvoiceTotals(invoice, invoiceItems);
        InvoiceEntity savedInvoice = invoiceRepository.save(invoice);

        return mapToResponse(savedInvoice, invoiceItems);
    }

    @Override
    public InvoicePreviewResponse previewInvoiceWithDiscounts(CreateInvoiceRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            BigDecimal itemPrice;
            BigDecimal defaultDiscount = BigDecimal.ZERO;
            DiscountType defaultDiscountType = DiscountType.PERCENTAGE;

            if (itemRequest.itemId() != null) {
                ItemEntity item = itemRepository.findByIdAndCompanyIdAndIsActiveTrue(
                                itemRequest.itemId(), currentUser.companyId())
                        .orElseThrow(() -> new RuntimeException("Item not found"));
                itemPrice = item.getUnitPrice();
                defaultDiscount = item.getDefaultDiscountValue();
                defaultDiscountType = item.getDefaultDiscountType();
            } else {
                ServiceTypeEntity serviceType = serviceTypeRepository.findByIdAndCompanyIdAndIsActiveTrue(
                                itemRequest.serviceTypeId(), currentUser.companyId())
                        .orElseThrow(() -> new RuntimeException("Service type not found"));
                itemPrice = serviceType.getBasePrice();
                defaultDiscount = serviceType.getDefaultDiscountValue();
                defaultDiscountType = serviceType.getDefaultDiscountType();
            }

            BigDecimal lineTotal = itemPrice.multiply(new BigDecimal(itemRequest.quantity()));
            BigDecimal discountAmount = calculateDiscountAmount(lineTotal, defaultDiscount, defaultDiscountType);
            subtotal = subtotal.add(lineTotal.subtract(discountAmount));
        }

        BigDecimal taxPercentage = request.taxPercentage() != null ? request.taxPercentage() : new BigDecimal("10.0");
        BigDecimal taxAmount = subtotal.multiply(taxPercentage.divide(new BigDecimal("100")));
        BigDecimal total = subtotal.add(taxAmount);

        return InvoicePreviewResponse.builder()
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .totalAmount(total)
                .estimatedSavings(calculateEstimatedSavings(request))
                .build();
    }

    @Override
    public String generateInvoiceNumber() {
        UserContextDto currentUser = UserContext.getUserContext();

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<InvoiceEntity> todayInvoices = invoiceRepository.findByDateRangeAndCompanyId(startOfDay, endOfDay, currentUser.companyId());
        int sequence = todayInvoices.size() + 1;

        return String.format("INV-%s-%04d", dateStr, sequence);
    }

    private void updateItemDiscountCalculations(InvoiceItemEntity item) {
        BigDecimal originalPrice = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
        BigDecimal discountAmount = calculateDiscountAmount(originalPrice, item.getDiscountValue(), item.getDiscountType());

        item.setDiscountAmount(discountAmount);
        item.setFinalPrice(originalPrice.subtract(discountAmount));
    }

    private BigDecimal calculateDiscountAmount(BigDecimal originalAmount, BigDecimal discountValue, DiscountType discountType) {
        if (discountValue == null || discountValue.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        return switch (discountType) {
            case PERCENTAGE -> originalAmount.multiply(discountValue.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            case FIXED_AMOUNT -> discountValue.min(originalAmount);
        };
    }

    private void recalculateInvoiceTotals(InvoiceEntity invoice, List<InvoiceItemEntity> items) {
        BigDecimal subtotal = items.stream()
                .map(InvoiceItemEntity::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overallDiscountAmount = calculateDiscountAmount(
                subtotal, invoice.getOverallDiscountValue(), invoice.getOverallDiscountType());

        BigDecimal discountedSubtotal = subtotal.subtract(overallDiscountAmount);
        BigDecimal taxAmount = discountedSubtotal.multiply(new BigDecimal("0.10"));
        BigDecimal total = discountedSubtotal.add(taxAmount);

        invoice.setSubtotal(subtotal);
        invoice.setOverallDiscountAmount(overallDiscountAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(total);
    }

    private InvoiceResponse mapToResponseWithDefaults(InvoiceEntity invoice, List<InvoiceItemEntity> invoiceItems) {
        invoiceItems.forEach(item -> {
            if (item.getDiscountValue() == null || item.getDiscountValue().equals(BigDecimal.ZERO)) {
                if (item.getItemId() != null) {
                    itemRepository.findById(item.getItemId()).ifPresent(itemEntity -> {
                        item.setDiscountValue(itemEntity.getDefaultDiscountValue());
                        item.setDiscountType(itemEntity.getDefaultDiscountType());
                        updateItemDiscountCalculations(item);
                    });
                } else if (item.getServiceTypeId() != null) {
                    serviceTypeRepository.findById(item.getServiceTypeId()).ifPresent(serviceType -> {
                        item.setDiscountValue(serviceType.getDefaultDiscountValue());
                        item.setDiscountType(serviceType.getDefaultDiscountType());
                        updateItemDiscountCalculations(item);
                    });
                }
            }
        });

        return mapToResponse(invoice, invoiceItems);
    }

    private BigDecimal calculateEstimatedSavings(CreateInvoiceRequest request) {
        return BigDecimal.ZERO;
    }

    private InvoiceResponse mapToResponse(InvoiceEntity invoice, List<InvoiceItemEntity> invoiceItems) {
        List<InvoiceItemResponse> itemResponses = invoiceItems.stream()
                .map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .discountValue(item.getDiscountValue())
                        .discountType(item.getDiscountType())
                        .discountAmount(item.getDiscountAmount())
                        .finalPrice(item.getFinalPrice())
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
                .overallDiscountValue(invoice.getOverallDiscountValue())
                .overallDiscountType(invoice.getOverallDiscountType())
                .overallDiscountAmount(invoice.getOverallDiscountAmount())
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

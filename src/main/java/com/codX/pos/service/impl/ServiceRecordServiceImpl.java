package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.dto.response.ServiceDetailResponse;
import com.codX.pos.dto.response.ServiceRecordResponse;
import com.codX.pos.entity.*;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ItemRepository;
import com.codX.pos.repository.ServiceDetailRepository;
import com.codX.pos.repository.ServiceRecordRepository;
import com.codX.pos.repository.ServiceTypeRepository;
import com.codX.pos.service.ServiceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final ServiceDetailRepository serviceDetailRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ServiceRecordEntity createServiceRecord(CreateServiceRecordRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.POS_USER && currentUser.role() != Role.BRANCH_ADMIN &&
                currentUser.role() != Role.COMPANY_ADMIN && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Insufficient permissions to create service record");
        }

        // Create service record
        ServiceRecordEntity serviceRecord = ServiceRecordEntity.builder()
                .vehicleId(request.vehicleId())
                .customerId(request.customerId())
                .serviceDate(request.serviceDate() != null ? request.serviceDate() : LocalDateTime.now())
                .currentMileage(request.currentMileage())
                .notes(request.notes())
                .status(request.status() != null ? request.status() : ServiceStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        ServiceRecordEntity savedServiceRecord = serviceRecordRepository.save(serviceRecord);

        // Create service details and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (request.serviceDetails() != null) {
            for (var serviceDetailRequest : request.serviceDetails()) {
                // Get service type
                ServiceTypeEntity serviceType = serviceTypeRepository.findByIdAndCompanyIdAndIsActiveTrue(
                                serviceDetailRequest.serviceTypeId(), currentUser.companyId())
                        .orElseThrow(() -> new RuntimeException("Service type not found"));

                // Create service detail for the service type
                BigDecimal servicePrice = serviceDetailRequest.unitPrice() != null ?
                        serviceDetailRequest.unitPrice() : serviceType.getBasePrice();
                BigDecimal serviceTotalPrice = servicePrice.multiply(new BigDecimal(serviceDetailRequest.quantity()));

                ServiceDetailEntity serviceDetail = ServiceDetailEntity.builder()
                        .serviceRecordId(savedServiceRecord.getId())
                        .serviceTypeId(serviceDetailRequest.serviceTypeId())
                        .quantity(serviceDetailRequest.quantity())
                        .unitPrice(servicePrice)
                        .totalPrice(serviceTotalPrice)
                        .type(ServiceDetailType.SERVICE)
                        .notes(serviceDetailRequest.notes())
                        .companyId(currentUser.companyId())
                        .branchId(currentUser.branchId())
                        .build();

                serviceDetailRepository.save(serviceDetail);
                totalAmount = totalAmount.add(serviceTotalPrice);

                // Create service details for items used in this service
                if (serviceDetailRequest.items() != null) {
                    for (var itemRequest : serviceDetailRequest.items()) {
                        ItemEntity item = itemRepository.findByIdAndCompanyIdAndIsActiveTrue(
                                        itemRequest.itemId(), currentUser.companyId())
                                .orElseThrow(() -> new RuntimeException("Item not found"));

                        // Check stock availability
                        if (item.getStockQuantity() < itemRequest.quantity()) {
                            throw new RuntimeException("Insufficient stock for item: " + item.getName());
                        }

                        BigDecimal itemPrice = itemRequest.unitPrice() != null ?
                                itemRequest.unitPrice() : item.getUnitPrice();
                        BigDecimal itemTotalPrice = itemPrice.multiply(new BigDecimal(itemRequest.quantity()));

                        ServiceDetailEntity itemDetail = ServiceDetailEntity.builder()
                                .serviceRecordId(savedServiceRecord.getId())
                                .serviceTypeId(serviceDetailRequest.serviceTypeId())
                                .itemId(itemRequest.itemId())
                                .quantity(itemRequest.quantity())
                                .unitPrice(itemPrice)
                                .totalPrice(itemTotalPrice)
                                .type(ServiceDetailType.ITEM)
                                .notes(itemRequest.notes())
                                .companyId(currentUser.companyId())
                                .branchId(currentUser.branchId())
                                .build();

                        serviceDetailRepository.save(itemDetail);
                        totalAmount = totalAmount.add(itemTotalPrice);

                        // Update item stock
                        item.setStockQuantity(item.getStockQuantity() - itemRequest.quantity());
                        itemRepository.save(item);
                    }
                }
            }
        }

        // Update service record with total amount
        savedServiceRecord.setTotalAmount(totalAmount);
        return serviceRecordRepository.save(savedServiceRecord);
    }

    @Override
    public List<ServiceRecordResponse> getServiceRecordsByVehicle(UUID vehicleId) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<ServiceRecordEntity> serviceRecords = serviceRecordRepository.findByVehicleIdAndCompanyIdOrderByServiceDateDesc(vehicleId, currentUser.companyId());

        return serviceRecords.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceRecordResponse> getServiceRecordsByCustomer(UUID customerId) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<ServiceRecordEntity> serviceRecords = serviceRecordRepository.findByCustomerIdAndCompanyIdOrderByServiceDateDesc(customerId, currentUser.companyId());

        return serviceRecords.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceRecordResponse> getServiceRecordsByBranch(UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();

        switch (currentUser.role()) {
            case SUPER_ADMIN:
                break;
            case COMPANY_ADMIN:
                break;
            case BRANCH_ADMIN:
            case POS_USER:
                if (!currentUser.branchId().equals(branchId)) {
                    throw new UnauthorizedException("You can only access service records from your own branch");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to access branch service records");
        }

        List<ServiceRecordEntity> serviceRecords = serviceRecordRepository.findByBranchIdOrderByServiceDateDesc(branchId);

        return serviceRecords.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceRecordResponse> getServiceRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<ServiceRecordEntity> serviceRecords = serviceRecordRepository.findByServiceDateBetweenAndCompanyIdOrderByServiceDateDesc(startDate, endDate, currentUser.companyId());

        return serviceRecords.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceRecordResponse getServiceRecordById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        ServiceRecordEntity serviceRecord = serviceRecordRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        return mapToResponse(serviceRecord);
    }

    @Override
    @Transactional
    public ServiceRecordEntity updateServiceRecord(UUID id, CreateServiceRecordRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        ServiceRecordEntity existingRecord = serviceRecordRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        // Update basic fields
        existingRecord.setVehicleId(request.vehicleId());
        existingRecord.setCustomerId(request.customerId());
        existingRecord.setServiceDate(request.serviceDate());
        existingRecord.setCurrentMileage(request.currentMileage());
        existingRecord.setNotes(request.notes());
        existingRecord.setStatus(request.status());

        // Delete existing service details and restore stock
        List<ServiceDetailEntity> existingDetails = serviceDetailRepository.findByServiceRecordIdAndCompanyId(id, currentUser.companyId());
        for (ServiceDetailEntity detail : existingDetails) {
            if (detail.getType() == ServiceDetailType.ITEM && detail.getItemId() != null) {
                ItemEntity item = itemRepository.findById(detail.getItemId()).orElse(null);
                if (item != null) {
                    item.setStockQuantity(item.getStockQuantity() + detail.getQuantity());
                    itemRepository.save(item);
                }
            }
        }
        serviceDetailRepository.deleteByServiceRecordIdAndCompanyId(id, currentUser.companyId());

        // Recreate service details with new data
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (request.serviceDetails() != null) {
            for (var serviceDetailRequest : request.serviceDetails()) {
                ServiceTypeEntity serviceType = serviceTypeRepository.findByIdAndCompanyIdAndIsActiveTrue(
                                serviceDetailRequest.serviceTypeId(), currentUser.companyId())
                        .orElseThrow(() -> new RuntimeException("Service type not found"));

                BigDecimal servicePrice = serviceDetailRequest.unitPrice() != null ?
                        serviceDetailRequest.unitPrice() : serviceType.getBasePrice();
                BigDecimal serviceTotalPrice = servicePrice.multiply(new BigDecimal(serviceDetailRequest.quantity()));

                ServiceDetailEntity serviceDetail = ServiceDetailEntity.builder()
                        .serviceRecordId(id)
                        .serviceTypeId(serviceDetailRequest.serviceTypeId())
                        .quantity(serviceDetailRequest.quantity())
                        .unitPrice(servicePrice)
                        .totalPrice(serviceTotalPrice)
                        .type(ServiceDetailType.SERVICE)
                        .notes(serviceDetailRequest.notes())
                        .companyId(currentUser.companyId())
                        .branchId(currentUser.branchId())
                        .build();

                serviceDetailRepository.save(serviceDetail);
                totalAmount = totalAmount.add(serviceTotalPrice);

                if (serviceDetailRequest.items() != null) {
                    for (var itemRequest : serviceDetailRequest.items()) {
                        ItemEntity item = itemRepository.findByIdAndCompanyIdAndIsActiveTrue(
                                        itemRequest.itemId(), currentUser.companyId())
                                .orElseThrow(() -> new RuntimeException("Item not found"));

                        if (item.getStockQuantity() < itemRequest.quantity()) {
                            throw new RuntimeException("Insufficient stock for item: " + item.getName());
                        }

                        BigDecimal itemPrice = itemRequest.unitPrice() != null ?
                                itemRequest.unitPrice() : item.getUnitPrice();
                        BigDecimal itemTotalPrice = itemPrice.multiply(new BigDecimal(itemRequest.quantity()));

                        ServiceDetailEntity itemDetail = ServiceDetailEntity.builder()
                                .serviceRecordId(id)
                                .serviceTypeId(serviceDetailRequest.serviceTypeId())
                                .itemId(itemRequest.itemId())
                                .quantity(itemRequest.quantity())
                                .unitPrice(itemPrice)
                                .totalPrice(itemTotalPrice)
                                .type(ServiceDetailType.ITEM)
                                .notes(itemRequest.notes())
                                .companyId(currentUser.companyId())
                                .branchId(currentUser.branchId())
                                .build();

                        serviceDetailRepository.save(itemDetail);
                        totalAmount = totalAmount.add(itemTotalPrice);

                        item.setStockQuantity(item.getStockQuantity() - itemRequest.quantity());
                        itemRepository.save(item);
                    }
                }
            }
        }

        existingRecord.setTotalAmount(totalAmount);
        return serviceRecordRepository.save(existingRecord);
    }

    @Override
    @Transactional
    public void deleteServiceRecord(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        ServiceRecordEntity serviceRecord = serviceRecordRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        // Restore stock for items
        List<ServiceDetailEntity> serviceDetails = serviceDetailRepository.findByServiceRecordIdAndCompanyId(id, currentUser.companyId());
        for (ServiceDetailEntity detail : serviceDetails) {
            if (detail.getType() == ServiceDetailType.ITEM && detail.getItemId() != null) {
                ItemEntity item = itemRepository.findById(detail.getItemId()).orElse(null);
                if (item != null) {
                    item.setStockQuantity(item.getStockQuantity() + detail.getQuantity());
                    itemRepository.save(item);
                }
            }
        }

        // Delete service details first
        serviceDetailRepository.deleteByServiceRecordIdAndCompanyId(id, currentUser.companyId());

        // Delete service record
        serviceRecordRepository.delete(serviceRecord);
    }

    private ServiceRecordResponse mapToResponse(ServiceRecordEntity serviceRecord) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<ServiceDetailEntity> serviceDetails = serviceDetailRepository.findByServiceRecordIdAndCompanyId(
                serviceRecord.getId(), currentUser.companyId());

        List<ServiceDetailResponse> serviceDetailResponses = serviceDetails.stream()
                .map(this::mapServiceDetailToResponse)
                .collect(Collectors.toList());

        return ServiceRecordResponse.builder()
                .id(serviceRecord.getId())
                .vehicleId(serviceRecord.getVehicleId())
                .customerId(serviceRecord.getCustomerId())
                .serviceDate(serviceRecord.getServiceDate())
                .currentMileage(serviceRecord.getCurrentMileage())
                .notes(serviceRecord.getNotes())
                .status(serviceRecord.getStatus())
                .totalAmount(serviceRecord.getTotalAmount())
                .invoiceId(serviceRecord.getInvoiceId())
                .serviceDetails(serviceDetailResponses)
                .build();
    }

    private ServiceDetailResponse mapServiceDetailToResponse(ServiceDetailEntity serviceDetail) {
        String serviceTypeName = null;
        String itemName = null;

        if (serviceDetail.getServiceTypeId() != null) {
            ServiceTypeEntity serviceType = serviceTypeRepository.findById(serviceDetail.getServiceTypeId()).orElse(null);
            if (serviceType != null) {
                serviceTypeName = serviceType.getName();
            }
        }

        if (serviceDetail.getItemId() != null) {
            ItemEntity item = itemRepository.findById(serviceDetail.getItemId()).orElse(null);
            if (item != null) {
                itemName = item.getName();
            }
        }

        return ServiceDetailResponse.builder()
                .id(serviceDetail.getId())
                .serviceTypeId(serviceDetail.getServiceTypeId())
                .serviceTypeName(serviceTypeName)
                .itemId(serviceDetail.getItemId())
                .itemName(itemName)
                .quantity(serviceDetail.getQuantity())
                .unitPrice(serviceDetail.getUnitPrice())
                .totalPrice(serviceDetail.getTotalPrice())
                .type(serviceDetail.getType())
                .notes(serviceDetail.getNotes())
                .build();
    }
}

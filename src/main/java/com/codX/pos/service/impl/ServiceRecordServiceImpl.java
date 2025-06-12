package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.dto.request.ServiceDetailRequest;
import com.codX.pos.dto.response.ServiceDetailResponse;
import com.codX.pos.dto.response.ServiceRecordResponse;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.ServiceRecordDetailEntity;
import com.codX.pos.entity.ServiceRecordEntity;
import com.codX.pos.entity.ServiceStatus;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ServiceDetailRepository;
import com.codX.pos.repository.ServiceRecordRepository;
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

    @Override
    @Transactional
    public ServiceRecordEntity createServiceRecord(CreateServiceRecordRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.POS_USER && currentUser.role() != Role.BRANCH_ADMIN &&
                currentUser.role() != Role.COMPANY_ADMIN && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Insufficient permissions to create service record");
        }

        // Calculate total amount from service details
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (request.serviceDetails() != null && !request.serviceDetails().isEmpty()) {
            totalAmount = request.serviceDetails().stream()
                    .map(detail -> detail.unitPrice().multiply(BigDecimal.valueOf(detail.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        ServiceRecordEntity serviceRecord = ServiceRecordEntity.builder()
                .vehicleId(request.vehicleId())
                .customerId(request.customerId())
                .serviceDate(request.serviceDate() != null ? request.serviceDate() : LocalDateTime.now())
                .currentMileage(request.currentMileage())
                .notes(request.notes())
                .status(request.status() != null ? request.status() : ServiceStatus.PENDING)
                .totalAmount(totalAmount)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        ServiceRecordEntity savedServiceRecord = serviceRecordRepository.save(serviceRecord);

        // Save service details
        if (request.serviceDetails() != null && !request.serviceDetails().isEmpty()) {
            List<ServiceRecordDetailEntity> serviceDetails = request.serviceDetails().stream()
                    .map(detail -> createServiceDetailEntity(detail, savedServiceRecord.getId(), currentUser))
                    .collect(Collectors.toList());

            serviceDetailRepository.saveAll(serviceDetails);
        }

        return savedServiceRecord;
    }

    @Override
    public List<ServiceRecordResponse> getServiceRecordsByVehicle(UUID vehicleId) {
        UserContextDto currentUser = UserContext.getUserContext();
        List<ServiceRecordEntity> serviceRecords = serviceRecordRepository
                .findByVehicleIdAndCompanyIdOrderByServiceDateDesc(vehicleId, currentUser.companyId());

        return serviceRecords.stream()
                .map(this::mapToResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceRecordResponse> getServiceRecordsByCustomer(UUID customerId) {
        UserContextDto currentUser = UserContext.getUserContext();
        List<ServiceRecordEntity> serviceRecords = serviceRecordRepository
                .findByCustomerIdAndCompanyIdOrderByServiceDateDesc(customerId, currentUser.companyId());

        return serviceRecords.stream()
                .map(this::mapToResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceRecordResponse> getServiceRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        UserContextDto currentUser = UserContext.getUserContext();
        List<ServiceRecordEntity> serviceRecords = serviceRecordRepository
                .findByDateRangeAndCompanyId(startDate, endDate, currentUser.companyId());

        return serviceRecords.stream()
                .map(this::mapToResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceRecordResponse getServiceRecordById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();
        ServiceRecordEntity serviceRecord = serviceRecordRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        return mapToResponseWithDetails(serviceRecord);
    }

    @Override
    @Transactional
    public ServiceRecordEntity updateServiceRecord(UUID id, CreateServiceRecordRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();
        ServiceRecordEntity existingRecord = serviceRecordRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        // Update basic fields
        existingRecord.setCurrentMileage(request.currentMileage());
        existingRecord.setNotes(request.notes());
        existingRecord.setStatus(request.status());

        // Delete existing service details
        serviceDetailRepository.deleteByServiceRecordId(id);

        // Calculate new total amount and save new service details
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (request.serviceDetails() != null && !request.serviceDetails().isEmpty()) {
            totalAmount = request.serviceDetails().stream()
                    .map(detail -> detail.unitPrice().multiply(BigDecimal.valueOf(detail.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ServiceRecordDetailEntity> serviceDetails = request.serviceDetails().stream()
                    .map(detail -> createServiceDetailEntity(detail, id, currentUser))
                    .collect(Collectors.toList());

            serviceDetailRepository.saveAll(serviceDetails);
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

        // Delete service details first
        serviceDetailRepository.deleteByServiceRecordId(id);

        // Delete service record
        serviceRecordRepository.delete(serviceRecord);
    }

    private ServiceRecordDetailEntity createServiceDetailEntity(ServiceDetailRequest detail, UUID serviceRecordId, UserContextDto currentUser) {
        BigDecimal totalPrice = detail.unitPrice().multiply(BigDecimal.valueOf(detail.quantity()));

        return ServiceRecordDetailEntity.builder()
                .serviceRecordId(serviceRecordId)
                .serviceTypeId(detail.serviceTypeId())
                .itemId(detail.itemId())
                .quantity(detail.quantity())
                .unitPrice(detail.unitPrice())
                .totalPrice(totalPrice)
                .notes(detail.notes())
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();
    }

    private ServiceRecordResponse mapToResponseWithDetails(ServiceRecordEntity serviceRecord) {
        List<ServiceRecordDetailEntity> serviceDetails = serviceDetailRepository
                .findByServiceRecordIdOrderByCreatedDate(serviceRecord.getId());

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
                .serviceDetails(serviceDetailResponses)
                .createdDate(serviceRecord.getCreatedDate())
                .build();
    }

    private ServiceDetailResponse mapServiceDetailToResponse(ServiceRecordDetailEntity serviceDetail) {
        return ServiceDetailResponse.builder()
                .id(serviceDetail.getId())
                .serviceRecordId(serviceDetail.getServiceRecordId())
                .serviceTypeId(serviceDetail.getServiceTypeId())
                .itemId(serviceDetail.getItemId())
                .quantity(serviceDetail.getQuantity())
                .unitPrice(serviceDetail.getUnitPrice())
                .totalPrice(serviceDetail.getTotalPrice())
                .notes(serviceDetail.getNotes())
                .createdDate(serviceDetail.getCreatedDate())
                .build();
    }
}

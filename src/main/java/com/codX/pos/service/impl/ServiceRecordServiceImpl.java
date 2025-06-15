package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.dto.response.ServiceRecordResponse;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.ServiceRecordEntity;
import com.codX.pos.entity.ServiceStatus;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ServiceRecordRepository;
import com.codX.pos.service.ServiceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;

    @Override
    @Transactional
    public ServiceRecordEntity createServiceRecord(CreateServiceRecordRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.POS_USER && currentUser.role() != Role.BRANCH_ADMIN &&
                currentUser.role() != Role.COMPANY_ADMIN && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Insufficient permissions to create service record");
        }

        ServiceRecordEntity serviceRecord = ServiceRecordEntity.builder()
                .vehicleId(request.vehicleId())
                .customerId(request.customerId())
                .serviceDate(request.serviceDate() != null ? request.serviceDate() : LocalDateTime.now())
                .currentMileage(request.currentMileage())
                .notes(request.notes())
                .status(request.status() != null ? request.status() : ServiceStatus.PENDING)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        return serviceRecordRepository.save(serviceRecord);
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
        ServiceRecordEntity existingRecord = serviceRecordRepository.findByIdAndCompanyId(id, UserContext.getUserContext().companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        existingRecord.setVehicleId(request.vehicleId());
        existingRecord.setCustomerId(request.customerId());
        existingRecord.setServiceDate(request.serviceDate());
        existingRecord.setCurrentMileage(request.currentMileage());
        existingRecord.setNotes(request.notes());
        existingRecord.setStatus(request.status());

        return serviceRecordRepository.save(existingRecord);
    }

    @Override
    @Transactional
    public void deleteServiceRecord(UUID id) {
        ServiceRecordEntity serviceRecord = serviceRecordRepository.findByIdAndCompanyId(id, UserContext.getUserContext().companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));

        serviceRecordRepository.delete(serviceRecord);
    }

    private ServiceRecordResponse mapToResponse(ServiceRecordEntity serviceRecord) {
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
                .build();
    }
}

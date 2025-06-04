package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.ServiceRecordEntity;
import com.codX.pos.entity.ServiceStatus;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ServiceRecordRepository;
import com.codX.pos.service.ServiceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;

    @Override
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
                .totalAmount(BigDecimal.ZERO) // Will be calculated based on service details
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .build();

        return serviceRecordRepository.save(serviceRecord);
    }

    @Override
    public List<ServiceRecordEntity> getServiceRecordsByVehicle(UUID vehicleId) {
        UserContextDto currentUser = UserContext.getUserContext();
        return serviceRecordRepository.findByVehicleIdAndCompanyIdOrderByServiceDateDesc(vehicleId, currentUser.companyId());
    }

    @Override
    public List<ServiceRecordEntity> getServiceRecordsByCustomer(UUID customerId) {
        UserContextDto currentUser = UserContext.getUserContext();
        return serviceRecordRepository.findByCustomerIdAndCompanyIdOrderByServiceDateDesc(customerId, currentUser.companyId());
    }

    @Override
    public List<ServiceRecordEntity> getServiceRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        UserContextDto currentUser = UserContext.getUserContext();
        return serviceRecordRepository.findByDateRangeAndCompanyId(startDate, endDate, currentUser.companyId());
    }

    @Override
    public ServiceRecordEntity getServiceRecordById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();
        return serviceRecordRepository.findByIdAndCompanyId(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service record not found"));
    }

    @Override
    public ServiceRecordEntity updateServiceRecord(UUID id, CreateServiceRecordRequest request) {
        ServiceRecordEntity existingRecord = getServiceRecordById(id);

        existingRecord.setCurrentMileage(request.currentMileage());
        existingRecord.setNotes(request.notes());
        existingRecord.setStatus(request.status());

        return serviceRecordRepository.save(existingRecord);
    }

    @Override
    public void deleteServiceRecord(UUID id) {
        ServiceRecordEntity serviceRecord = getServiceRecordById(id);
        serviceRecordRepository.delete(serviceRecord);
    }
}

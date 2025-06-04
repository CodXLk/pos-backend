package com.codX.pos.service;

import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.entity.ServiceRecordEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ServiceRecordService {
    ServiceRecordEntity createServiceRecord(CreateServiceRecordRequest request);
    List<ServiceRecordEntity> getServiceRecordsByVehicle(UUID vehicleId);
    List<ServiceRecordEntity> getServiceRecordsByCustomer(UUID customerId);
    List<ServiceRecordEntity> getServiceRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    ServiceRecordEntity getServiceRecordById(UUID id);
    ServiceRecordEntity updateServiceRecord(UUID id, CreateServiceRecordRequest request);
    void deleteServiceRecord(UUID id);
}

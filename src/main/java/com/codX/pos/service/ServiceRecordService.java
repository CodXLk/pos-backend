package com.codX.pos.service;

import com.codX.pos.dto.request.CreateServiceRecordRequest;
import com.codX.pos.dto.response.ServiceRecordResponse;
import com.codX.pos.entity.ServiceRecordEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ServiceRecordService {
    ServiceRecordEntity createServiceRecord(CreateServiceRecordRequest request);
    List<ServiceRecordResponse> getServiceRecordsByVehicle(UUID vehicleId);
    List<ServiceRecordResponse> getServiceRecordsByCustomer(UUID customerId);
    List<ServiceRecordResponse> getServiceRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    ServiceRecordResponse getServiceRecordById(UUID id);
    ServiceRecordEntity updateServiceRecord(UUID id, CreateServiceRecordRequest request);
    void deleteServiceRecord(UUID id);
}

package com.codX.pos.service;

import com.codX.pos.dto.request.CreateServiceTypeRequest;
import com.codX.pos.dto.request.DiscountRequest;
import com.codX.pos.entity.ServiceTypeEntity;

import java.util.List;
import java.util.UUID;

public interface ServiceTypeService {
    ServiceTypeEntity createServiceType(CreateServiceTypeRequest request);
    List<ServiceTypeEntity> getServiceTypesByCategory(UUID categoryId);
    List<ServiceTypeEntity> getServiceTypesByCompany(UUID companyId);
    List<ServiceTypeEntity> getServiceTypesByBranch(UUID branchId);
    ServiceTypeEntity getServiceTypeById(UUID id);
    ServiceTypeEntity updateServiceType(UUID id, CreateServiceTypeRequest request);
    ServiceTypeEntity updateDefaultDiscount(UUID id, DiscountRequest discountRequest); // NEW
    void deactivateServiceType(UUID id);
}

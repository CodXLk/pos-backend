package com.codX.pos.service;

import com.codX.pos.dto.request.CreateServiceCategoryRequest;
import com.codX.pos.entity.ServiceCategoryEntity;

import java.util.List;
import java.util.UUID;

public interface ServiceCategoryService {
    ServiceCategoryEntity createServiceCategory(CreateServiceCategoryRequest request);
    List<ServiceCategoryEntity> getServiceCategoriesByCompany(UUID companyId);
    List<ServiceCategoryEntity> getServiceCategoriesByBranch(UUID companyId, UUID branchId);
    ServiceCategoryEntity getServiceCategoryById(UUID id);
    ServiceCategoryEntity updateServiceCategory(UUID id, CreateServiceCategoryRequest request);
    void deactivateServiceCategory(UUID id);
}

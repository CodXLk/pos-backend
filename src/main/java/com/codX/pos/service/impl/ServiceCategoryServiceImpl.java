package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateServiceCategoryRequest;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.ServiceCategoryEntity;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ServiceCategoryRepository;
import com.codX.pos.service.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    public ServiceCategoryEntity createServiceCategory(CreateServiceCategoryRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.BRANCH_ADMIN && currentUser.role() != Role.COMPANY_ADMIN &&
                currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Branch Admin or above can create service categories");
        }

        // Check if category name already exists in the branch
        if (serviceCategoryRepository.existsByNameAndCompanyIdAndBranchId(
                request.name(), currentUser.companyId(), currentUser.branchId())) {
            throw new RuntimeException("Service category with name '" + request.name() + "' already exists in this branch");
        }

        ServiceCategoryEntity serviceCategory = ServiceCategoryEntity.builder()
                .name(request.name())
                .description(request.description())
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .isActive(true)
                .build();

        return serviceCategoryRepository.save(serviceCategory);
    }

    @Override
    public List<ServiceCategoryEntity> getServiceCategoriesByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company service categories");
        }

        return serviceCategoryRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    public List<ServiceCategoryEntity> getServiceCategoriesByBranch(UUID companyId, UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company service categories");
        }

        return serviceCategoryRepository.findByCompanyIdAndBranchIdAndIsActiveTrue(companyId, branchId);
    }

    @Override
    public ServiceCategoryEntity getServiceCategoryById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        return serviceCategoryRepository.findByIdAndCompanyIdAndIsActiveTrue(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service category not found"));
    }

    @Override
    public ServiceCategoryEntity updateServiceCategory(UUID id, CreateServiceCategoryRequest request) {
        ServiceCategoryEntity existingCategory = getServiceCategoryById(id);

        existingCategory.setName(request.name());
        existingCategory.setDescription(request.description());

        return serviceCategoryRepository.save(existingCategory);
    }

    @Override
    public void deactivateServiceCategory(UUID id) {
        ServiceCategoryEntity serviceCategory = getServiceCategoryById(id);
        serviceCategory.setActive(false);
        serviceCategoryRepository.save(serviceCategory);
    }
}

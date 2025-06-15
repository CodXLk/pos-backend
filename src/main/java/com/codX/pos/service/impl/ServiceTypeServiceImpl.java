package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateServiceTypeRequest;
import com.codX.pos.dto.request.DiscountRequest;
import com.codX.pos.entity.DiscountType;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.ServiceTypeEntity;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ServiceTypeRepository;
import com.codX.pos.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    @Override
    public ServiceTypeEntity createServiceType(CreateServiceTypeRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.BRANCH_ADMIN && currentUser.role() != Role.COMPANY_ADMIN &&
                currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Branch Admin or above can create service types");
        }

        if (serviceTypeRepository.existsByNameAndCompanyIdAndBranchId(
                request.name(), currentUser.companyId(), currentUser.branchId())) {
            throw new RuntimeException("Service type with name '" + request.name() + "' already exists in this branch");
        }

        ServiceTypeEntity serviceType = ServiceTypeEntity.builder()
                .name(request.name())
                .description(request.description())
                .basePrice(request.basePrice())
                .estimatedDurationMinutes(request.estimatedDurationMinutes())
                .serviceCategoryId(request.serviceCategoryId())
                .defaultDiscountValue(BigDecimal.ZERO)
                .defaultDiscountType(DiscountType.PERCENTAGE)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .isActive(true)
                .build();

        return serviceTypeRepository.save(serviceType);
    }

    @Override
    public List<ServiceTypeEntity> getServiceTypesByCategory(UUID categoryId) {
        UserContextDto currentUser = UserContext.getUserContext();
        return serviceTypeRepository.findByServiceCategoryIdAndCompanyIdAndIsActiveTrue(categoryId, currentUser.companyId());
    }

    @Override
    public List<ServiceTypeEntity> getServiceTypesByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company service types");
        }

        return serviceTypeRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    public List<ServiceTypeEntity> getServiceTypesByBranch(UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();

        switch (currentUser.role()) {
            case SUPER_ADMIN:
                break;
            case COMPANY_ADMIN:
                break;
            case BRANCH_ADMIN:
            case POS_USER:
                if (!currentUser.branchId().equals(branchId)) {
                    throw new UnauthorizedException("You can only access service types from your own branch");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to access branch service types");
        }

        return serviceTypeRepository.findByBranchIdAndIsActiveTrue(branchId);
    }

    @Override
    public ServiceTypeEntity getServiceTypeById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        return serviceTypeRepository.findByIdAndCompanyIdAndIsActiveTrue(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Service type not found"));
    }

    @Override
    public ServiceTypeEntity updateServiceType(UUID id, CreateServiceTypeRequest request) {
        ServiceTypeEntity existingServiceType = getServiceTypeById(id);

        existingServiceType.setName(request.name());
        existingServiceType.setDescription(request.description());
        existingServiceType.setBasePrice(request.basePrice());
        existingServiceType.setEstimatedDurationMinutes(request.estimatedDurationMinutes());
        existingServiceType.setServiceCategoryId(request.serviceCategoryId());

        return serviceTypeRepository.save(existingServiceType);
    }

    @Override
    public ServiceTypeEntity updateDefaultDiscount(UUID id, DiscountRequest discountRequest) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.BRANCH_ADMIN && currentUser.role() != Role.COMPANY_ADMIN &&
                currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Branch Admin or above can update service type discounts");
        }

        ServiceTypeEntity serviceType = getServiceTypeById(id);

        serviceType.setDefaultDiscountValue(discountRequest.value() != null ? discountRequest.value() : BigDecimal.ZERO);
        serviceType.setDefaultDiscountType(discountRequest.type());

        return serviceTypeRepository.save(serviceType);
    }

    @Override
    public void deactivateServiceType(UUID id) {
        ServiceTypeEntity serviceType = getServiceTypeById(id);
        serviceType.setActive(false);
        serviceTypeRepository.save(serviceType);
    }
}

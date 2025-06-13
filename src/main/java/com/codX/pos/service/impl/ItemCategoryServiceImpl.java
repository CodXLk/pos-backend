package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateItemCategoryRequest;
import com.codX.pos.entity.ItemCategoryEntity;
import com.codX.pos.entity.Role;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ItemCategoryRepository;
import com.codX.pos.service.ItemCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private final ItemCategoryRepository itemCategoryRepository;

    @Override
    public ItemCategoryEntity createItemCategory(CreateItemCategoryRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.BRANCH_ADMIN && currentUser.role() != Role.COMPANY_ADMIN &&
                currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Branch Admin or above can create item categories");
        }

        // Check if category name already exists in the branch
        if (itemCategoryRepository.existsByNameAndCompanyIdAndBranchId(
                request.name(), currentUser.companyId(), currentUser.branchId())) {
            throw new RuntimeException("Item category with name '" + request.name() + "' already exists in this branch");
        }

        ItemCategoryEntity itemCategory = ItemCategoryEntity.builder()
                .name(request.name())
                .description(request.description())
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .isActive(true)
                .build();

        return itemCategoryRepository.save(itemCategory);
    }

    @Override
    public List<ItemCategoryEntity> getItemCategoriesByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company item categories");
        }

        return itemCategoryRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    public List<ItemCategoryEntity> getItemCategoriesByBranch(UUID companyId, UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company item categories");
        }

        return itemCategoryRepository.findByCompanyIdAndBranchIdAndIsActiveTrue(companyId, branchId);
    }

    @Override
    public ItemCategoryEntity getItemCategoryById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        return itemCategoryRepository.findByIdAndCompanyIdAndIsActiveTrue(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Item category not found"));
    }

    @Override
    public ItemCategoryEntity updateItemCategory(UUID id, CreateItemCategoryRequest request) {
        ItemCategoryEntity existingCategory = getItemCategoryById(id);

        existingCategory.setName(request.name());
        existingCategory.setDescription(request.description());

        return itemCategoryRepository.save(existingCategory);
    }

    @Override
    public void deactivateItemCategory(UUID id) {
        ItemCategoryEntity itemCategory = getItemCategoryById(id);
        itemCategory.setActive(false);
        itemCategoryRepository.save(itemCategory);
    }
}
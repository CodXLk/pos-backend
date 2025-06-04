package com.codX.pos.service;

import com.codX.pos.dto.request.CreateItemCategoryRequest;
import com.codX.pos.entity.ItemCategoryEntity;

import java.util.List;
import java.util.UUID;

public interface ItemCategoryService {
    ItemCategoryEntity createItemCategory(CreateItemCategoryRequest request);
    List<ItemCategoryEntity> getItemCategoriesByCompany(UUID companyId);
    List<ItemCategoryEntity> getItemCategoriesByBranch(UUID companyId, UUID branchId);
    ItemCategoryEntity getItemCategoryById(UUID id);
    ItemCategoryEntity updateItemCategory(UUID id, CreateItemCategoryRequest request);
    void deactivateItemCategory(UUID id);
}

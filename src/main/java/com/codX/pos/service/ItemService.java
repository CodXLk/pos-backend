package com.codX.pos.service;

import com.codX.pos.dto.request.CreateItemRequest;
import com.codX.pos.entity.ItemEntity;

import java.util.List;
import java.util.UUID;

public interface ItemService {
    ItemEntity createItem(CreateItemRequest request);
    List<ItemEntity> getItemsByCategory(UUID categoryId);
    List<ItemEntity> getItemsByCompany(UUID companyId);
    ItemEntity getItemById(UUID id);
    ItemEntity updateItem(UUID id, CreateItemRequest request);
    void deactivateItem(UUID id);
}

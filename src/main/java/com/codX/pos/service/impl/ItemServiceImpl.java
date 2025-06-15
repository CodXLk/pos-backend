package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateItemRequest;
import com.codX.pos.dto.request.DiscountRequest;
import com.codX.pos.entity.DiscountType;
import com.codX.pos.entity.ItemEntity;
import com.codX.pos.entity.Role;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.ItemRepository;
import com.codX.pos.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemEntity createItem(CreateItemRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.BRANCH_ADMIN && currentUser.role() != Role.COMPANY_ADMIN &&
                currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Branch Admin or above can create items");
        }

        if (itemRepository.existsByNameAndCompanyIdAndBranchId(
                request.name(), currentUser.companyId(), currentUser.branchId())) {
            throw new RuntimeException("Item with name '" + request.name() + "' already exists in this branch");
        }

        ItemEntity item = ItemEntity.builder()
                .name(request.name())
                .description(request.description())
                .unitPrice(request.unitPrice())
                .unit(request.unit())
                .stockQuantity(request.stockQuantity())
                .minStockLevel(request.minStockLevel())
                .itemCategoryId(request.itemCategoryId())
                .defaultDiscountValue(BigDecimal.ZERO)
                .defaultDiscountType(DiscountType.PERCENTAGE)
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .isActive(true)
                .build();

        return itemRepository.save(item);
    }

    @Override
    public List<ItemEntity> getItemsByCategory(UUID categoryId) {
        UserContextDto currentUser = UserContext.getUserContext();
        return itemRepository.findByItemCategoryIdAndCompanyIdAndIsActiveTrue(categoryId, currentUser.companyId());
    }

    @Override
    public List<ItemEntity> getItemsByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company items");
        }

        return itemRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    public List<ItemEntity> getItemsByBranch(UUID branchId) {
        UserContextDto currentUser = UserContext.getUserContext();

        switch (currentUser.role()) {
            case SUPER_ADMIN:
                break;
            case COMPANY_ADMIN:
                break;
            case BRANCH_ADMIN:
            case POS_USER:
                if (!currentUser.branchId().equals(branchId)) {
                    throw new UnauthorizedException("You can only access items from your own branch");
                }
                break;
            default:
                throw new UnauthorizedException("Insufficient permissions to access branch items");
        }

        return itemRepository.findByBranchIdAndIsActiveTrue(branchId);
    }

    @Override
    public ItemEntity getItemById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        return itemRepository.findByIdAndCompanyIdAndIsActiveTrue(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public ItemEntity updateItem(UUID id, CreateItemRequest request) {
        ItemEntity existingItem = getItemById(id);

        existingItem.setName(request.name());
        existingItem.setDescription(request.description());
        existingItem.setUnitPrice(request.unitPrice());
        existingItem.setUnit(request.unit());
        existingItem.setStockQuantity(request.stockQuantity());
        existingItem.setMinStockLevel(request.minStockLevel());
        existingItem.setItemCategoryId(request.itemCategoryId());

        return itemRepository.save(existingItem);
    }

    @Override
    public ItemEntity updateDefaultDiscount(UUID id, DiscountRequest discountRequest) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.BRANCH_ADMIN && currentUser.role() != Role.COMPANY_ADMIN &&
                currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Branch Admin or above can update item discounts");
        }

        ItemEntity item = getItemById(id);

        item.setDefaultDiscountValue(discountRequest.value() != null ? discountRequest.value() : BigDecimal.ZERO);
        item.setDefaultDiscountType(discountRequest.type());

        return itemRepository.save(item);
    }

    @Override
    public void deactivateItem(UUID id) {
        ItemEntity item = getItemById(id);
        item.setActive(false);
        itemRepository.save(item);
    }
}

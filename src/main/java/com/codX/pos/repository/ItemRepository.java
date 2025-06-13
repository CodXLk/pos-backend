package com.codX.pos.repository;

import com.codX.pos.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
    List<ItemEntity> findByCompanyIdAndIsActiveTrue(UUID companyId);
    List<ItemEntity> findByCompanyIdAndBranchIdAndIsActiveTrue(UUID companyId, UUID branchId);
    List<ItemEntity> findByBranchIdAndIsActiveTrue(UUID branchId); // NEW METHOD
    List<ItemEntity> findByItemCategoryIdAndCompanyIdAndIsActiveTrue(UUID itemCategoryId, UUID companyId);
    Optional<ItemEntity> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
    boolean existsByNameAndCompanyIdAndBranchId(String name, UUID companyId, UUID branchId);
}

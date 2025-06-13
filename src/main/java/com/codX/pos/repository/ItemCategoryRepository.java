package com.codX.pos.repository;

import com.codX.pos.entity.ItemCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategoryEntity, UUID> {
    List<ItemCategoryEntity> findByCompanyIdAndIsActiveTrue(UUID companyId);
    List<ItemCategoryEntity> findByCompanyIdAndBranchIdAndIsActiveTrue(UUID companyId, UUID branchId);
    Optional<ItemCategoryEntity> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
    boolean existsByNameAndCompanyIdAndBranchId(String name, UUID companyId, UUID branchId);
}

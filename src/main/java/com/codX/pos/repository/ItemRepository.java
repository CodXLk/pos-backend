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
    Optional<ItemEntity> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
    List<ItemEntity> findByCategoryAndCompanyIdAndIsActiveTrue(String category, UUID companyId);
}

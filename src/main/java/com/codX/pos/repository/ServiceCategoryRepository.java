package com.codX.pos.repository;

import com.codX.pos.entity.ServiceCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategoryEntity, UUID> {
    List<ServiceCategoryEntity> findByCompanyIdAndIsActiveTrue(UUID companyId);
    List<ServiceCategoryEntity> findByCompanyIdAndBranchIdAndIsActiveTrue(UUID companyId, UUID branchId);
    Optional<ServiceCategoryEntity> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
    boolean existsByNameAndCompanyIdAndBranchId(String name, UUID companyId, UUID branchId);
}

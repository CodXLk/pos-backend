package com.codX.pos.repository;

import com.codX.pos.entity.ServiceTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceTypeEntity, UUID> {
    List<ServiceTypeEntity> findByCompanyIdAndIsActiveTrue(UUID companyId);
    List<ServiceTypeEntity> findByCompanyIdAndBranchIdAndIsActiveTrue(UUID companyId, UUID branchId);
    Optional<ServiceTypeEntity> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
}

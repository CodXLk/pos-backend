package com.codX.pos.repository;

import com.codX.pos.entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, UUID> {
    List<BranchEntity> findByCompanyIdAndIsActiveTrue(UUID companyId);
    long countByCompanyIdAndIsActiveTrue(UUID companyId);
}

package com.codX.pos.repository;

import com.codX.pos.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID> {
    Optional<CompanyEntity> findByIdAndIsActiveTrue(UUID id);
    List<CompanyEntity> findByIsActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}

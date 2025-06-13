package com.codX.pos.repository;

import com.codX.pos.entity.Role;
import com.codX.pos.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByIdAndIsActiveTrue(UUID id);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    List<UserEntity> findByCompanyIdAndIsActiveTrue(UUID companyId);
    List<UserEntity> findByBranchIdAndIsActiveTrue(UUID branchId);
    List<UserEntity> findByCompanyIdAndBranchIdAndIsActiveTrue(UUID companyId, UUID branchId);
    List<UserEntity> findByRoleAndCompanyIdAndIsActiveTrue(Role role, UUID companyId);
    List<UserEntity> findByRoleAndBranchIdAndIsActiveTrue(Role role, UUID branchId);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    boolean existsByUserName(String userName);
    boolean existsByPhoneNumber(String phoneNumber);
}

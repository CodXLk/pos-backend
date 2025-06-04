package com.codX.pos.repository;

import com.codX.pos.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID> {
    List<VehicleEntity> findByCustomerIdAndCompanyIdAndIsActiveTrue(UUID customerId, UUID companyId);
    List<VehicleEntity> findByCompanyIdAndIsActiveTrue(UUID companyId);
    List<VehicleEntity> findByCompanyIdAndBranchIdAndIsActiveTrue(UUID companyId, UUID branchId);
    Optional<VehicleEntity> findByVehicleNumberAndCompanyIdAndIsActiveTrue(String vehicleNumber, UUID companyId);
    Optional<VehicleEntity> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
    boolean existsByVehicleNumberAndCompanyId(String vehicleNumber, UUID companyId);

    @Query("SELECT v FROM VehicleEntity v WHERE v.vehicleNumber LIKE %:vehicleNumber% AND v.companyId = :companyId AND v.isActive = true")
    List<VehicleEntity> searchByVehicleNumber(@Param("vehicleNumber") String vehicleNumber, @Param("companyId") UUID companyId);
}

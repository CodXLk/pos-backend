package com.codX.pos.repository;

import com.codX.pos.entity.ServiceRecordEntity;
import com.codX.pos.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecordEntity, UUID> {
    List<ServiceRecordEntity> findByVehicleIdAndCompanyIdOrderByServiceDateDesc(UUID vehicleId, UUID companyId);
    List<ServiceRecordEntity> findByCustomerIdAndCompanyIdOrderByServiceDateDesc(UUID customerId, UUID companyId);
    List<ServiceRecordEntity> findByCompanyIdAndBranchIdOrderByServiceDateDesc(UUID companyId, UUID branchId);
    List<ServiceRecordEntity> findByBranchIdOrderByServiceDateDesc(UUID branchId);
    List<ServiceRecordEntity> findByServiceDateBetweenAndCompanyIdOrderByServiceDateDesc(LocalDateTime startDate, LocalDateTime endDate, UUID companyId); // ADD THIS METHOD
    Optional<ServiceRecordEntity> findByIdAndCompanyId(UUID id, UUID companyId);
    List<ServiceRecordEntity> findByStatusAndCompanyIdAndBranchId(ServiceStatus status, UUID companyId, UUID branchId);

    @Query("SELECT sr FROM ServiceRecordEntity sr WHERE sr.serviceDate BETWEEN :startDate AND :endDate AND sr.companyId = :companyId ORDER BY sr.serviceDate DESC")
    List<ServiceRecordEntity> findByDateRangeAndCompanyId(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate,
                                                          @Param("companyId") UUID companyId);
}

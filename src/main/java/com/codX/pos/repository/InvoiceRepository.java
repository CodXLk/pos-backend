package com.codX.pos.repository;

import com.codX.pos.entity.InvoiceEntity;
import com.codX.pos.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {
    List<InvoiceEntity> findByCustomerIdAndCompanyIdOrderByInvoiceDateDesc(UUID customerId, UUID companyId);
    List<InvoiceEntity> findByCompanyIdAndBranchIdOrderByInvoiceDateDesc(UUID companyId, UUID branchId);
    Optional<InvoiceEntity> findByIdAndCompanyId(UUID id, UUID companyId);
    List<InvoiceEntity> findByStatusAndCompanyIdAndBranchId(InvoiceStatus status, UUID companyId, UUID branchId);
    Optional<InvoiceEntity> findByInvoiceNumberAndCompanyId(String invoiceNumber, UUID companyId);
    Optional<InvoiceEntity> findByServiceRecordIdAndCompanyId(UUID serviceRecordId, UUID companyId);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.invoiceDate BETWEEN :startDate AND :endDate AND i.companyId = :companyId ORDER BY i.invoiceDate DESC")
    List<InvoiceEntity> findByDateRangeAndCompanyId(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("companyId") UUID companyId);
}

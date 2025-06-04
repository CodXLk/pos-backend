package com.codX.pos.repository;

import com.codX.pos.entity.InvoiceEntity;
import com.codX.pos.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

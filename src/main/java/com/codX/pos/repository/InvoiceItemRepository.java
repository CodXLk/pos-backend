package com.codX.pos.repository;

import com.codX.pos.entity.InvoiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItemEntity, UUID> {
    List<InvoiceItemEntity> findByInvoiceIdAndCompanyId(UUID invoiceId, UUID companyId);
    void deleteByInvoiceIdAndCompanyId(UUID invoiceId, UUID companyId);
}

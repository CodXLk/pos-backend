package com.codX.pos.repository;

import com.codX.pos.entity.ServiceRecordDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceDetailRepository extends JpaRepository<ServiceRecordDetailEntity, UUID> {
    List<ServiceRecordDetailEntity> findByServiceRecordIdOrderByCreatedDate(UUID serviceRecordId);
    List<ServiceRecordDetailEntity> findByServiceRecordIdAndCompanyId(UUID serviceRecordId, UUID companyId);
    void deleteByServiceRecordId(UUID serviceRecordId);
}

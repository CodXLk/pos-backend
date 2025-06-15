package com.codX.pos.repository;

import com.codX.pos.entity.ServiceDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceDetailRepository extends JpaRepository<ServiceDetailEntity, UUID> {
    List<ServiceDetailEntity> findByServiceRecordIdAndCompanyId(UUID serviceRecordId, UUID companyId);
    List<ServiceDetailEntity> findByServiceRecordIdOrderByTypeDesc(UUID serviceRecordId);
    void deleteByServiceRecordIdAndCompanyId(UUID serviceRecordId, UUID companyId);
}

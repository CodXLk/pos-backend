package com.codX.pos.service;

import com.codX.pos.dto.Company;
import com.codX.pos.entity.CompanyEntity;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyEntity create(Company company);
    List<CompanyEntity> getAllActiveCompanies();
    CompanyEntity getCompanyById(UUID id);
    CompanyEntity updateCompany(UUID id, Company company);
    CompanyEntity updateMaxBranches(UUID companyId, int maxBranches);
    void deactivateCompany(UUID id);
}

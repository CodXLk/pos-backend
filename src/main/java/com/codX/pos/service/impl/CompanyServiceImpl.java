package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.Company;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.entity.CompanyEntity;
import com.codX.pos.entity.Role;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.CompanyRepository;
import com.codX.pos.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CompanyEntity create(Company company) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser == null || currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Super Admin can create companies");
        }

        // Check for duplicate company name
        if (companyRepository.existsByNameIgnoreCase(company.name())) {
            throw new RuntimeException("Company with name '" + company.name() + "' already exists");
        }

        CompanyEntity companyEntity = CompanyEntity.builder()
                .name(company.name())
                .email(company.email())
                .address(company.address())
                .logoUrl(company.logoUrl())
                .contactNumber(company.contactNumber())
                .status(company.status())
                .maxBranches(5) // Default max branches
                .isActive(true)
                .build();

        return companyRepository.save(companyEntity);
    }

    @Override
    public List<CompanyEntity> getAllActiveCompanies() {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Super Admin can view all companies");
        }

        return companyRepository.findByIsActiveTrue();
    }

    @Override
    public CompanyEntity getCompanyById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(id)) {
            throw new UnauthorizedException("Access denied to company data");
        }

        return companyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    @Override
    public CompanyEntity updateCompany(UUID id, Company company) {
        if (companyRepository.existsByNameIgnoreCase(company.name())) {
            throw new RuntimeException("Company with name '" + company.name() + "' already exists");
        }
        CompanyEntity existingCompany = getCompanyById(id);

        existingCompany.setName(company.name());
        existingCompany.setEmail(company.email());
        existingCompany.setAddress(company.address());
        existingCompany.setLogoUrl(company.logoUrl());
        existingCompany.setContactNumber(company.contactNumber());

        return companyRepository.save(existingCompany);
    }

    @Override
    public CompanyEntity updateMaxBranches(UUID companyId, int maxBranches) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Super Admin can update branch limits");
        }

        CompanyEntity company = getCompanyById(companyId);
        company.setMaxBranches(maxBranches);

        return companyRepository.save(company);
    }

    @Override
    public void deactivateCompany(UUID id) {
        CompanyEntity company = getCompanyById(id);
        company.setActive(false);
        companyRepository.save(company);
    }
}

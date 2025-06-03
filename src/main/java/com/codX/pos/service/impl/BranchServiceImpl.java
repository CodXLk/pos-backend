package com.codX.pos.service.impl;

import com.codX.pos.dto.Branch;
import com.codX.pos.entity.BranchEntity;
import com.codX.pos.entity.CompanyEntity;
import com.codX.pos.repository.BranchRepository;
import com.codX.pos.repository.CompanyRepository;
import com.codX.pos.service.BranchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public BranchEntity create(Branch branch) {
        // Validate company exists
        CompanyEntity company = companyRepository.findById(branch.companyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Check branch limit (assuming max 5 branches per company)
        long currentBranchCount = branchRepository.countByCompanyIdAndIsActiveTrue(branch.companyId());
        if (currentBranchCount >= 5) { // You can make this configurable
            throw new RuntimeException("Maximum branch limit reached for this company");
        }

        BranchEntity branchEntity = objectMapper.convertValue(branch, BranchEntity.class);
        branchEntity.setActive(true);

        return branchRepository.save(branchEntity);
    }

    @Override
    public List<BranchEntity> getBranchesByCompany(UUID companyId) {
        return branchRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }
}

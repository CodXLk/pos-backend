package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateBranchRequest;
import com.codX.pos.entity.BranchEntity;
import com.codX.pos.entity.CompanyEntity;
import com.codX.pos.entity.Role;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.BranchRepository;
import com.codX.pos.repository.CompanyRepository;
import com.codX.pos.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;

    @Override
    public BranchEntity createBranch(CreateBranchRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.COMPANY_ADMIN && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only Company Admin or Super Admin can create branches");
        }

        // For Company Admin, use their company; for Super Admin, they can specify any company
        UUID companyId = currentUser.role() == Role.SUPER_ADMIN ?
                request.companyId() : currentUser.companyId();

        // Validate company exists
        CompanyEntity company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Check branch limit
        long currentBranchCount = branchRepository.countByCompanyIdAndIsActiveTrue(companyId);
        if (currentBranchCount >= company.getMaxBranches()) {
            throw new RuntimeException("Maximum branch limit reached for this company");
        }

        BranchEntity branchEntity = BranchEntity.builder()
                .name(request.name())
                .address(request.address())
                .contactNumber(request.contactNumber())
                .companyId(companyId)
                .branchAdminId(request.branchAdminId())
                .isActive(true)
                .build();

        return branchRepository.save(branchEntity);
    }

    @Override
    public List<BranchEntity> getBranchesByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company branches");
        }

        return branchRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    public BranchEntity getBranchById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // Validate access
        if (currentUser.role() != Role.SUPER_ADMIN &&
                !currentUser.companyId().equals(branch.getCompanyId())) {
            throw new UnauthorizedException("Access denied to branch");
        }

        return branch;
    }

    @Override
    public BranchEntity updateBranch(UUID id, CreateBranchRequest request) {
        BranchEntity existingBranch = getBranchById(id);

        existingBranch.setName(request.name());
        existingBranch.setAddress(request.address());
        existingBranch.setContactNumber(request.contactNumber());

        if (request.branchAdminId() != null) {
            existingBranch.setBranchAdminId(request.branchAdminId());
        }

        return branchRepository.save(existingBranch);
    }

    @Override
    public void deactivateBranch(UUID id) {
        BranchEntity branch = getBranchById(id);
        branch.setActive(false);
        branchRepository.save(branch);
    }
}

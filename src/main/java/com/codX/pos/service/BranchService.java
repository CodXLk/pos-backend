package com.codX.pos.service;

import com.codX.pos.dto.request.CreateBranchRequest;
import com.codX.pos.entity.BranchEntity;

import java.util.List;
import java.util.UUID;

public interface BranchService {
    BranchEntity createBranch(CreateBranchRequest request); // Changed method name and parameter
    List<BranchEntity> getBranchesByCompany(UUID companyId);
    BranchEntity getBranchById(UUID id);
    BranchEntity updateBranch(UUID id, CreateBranchRequest request);
    void deactivateBranch(UUID id);
}

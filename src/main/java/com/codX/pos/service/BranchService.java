package com.codX.pos.service;

import com.codX.pos.dto.Branch;
import com.codX.pos.entity.BranchEntity;

import java.util.List;
import java.util.UUID;

public interface BranchService {
    BranchEntity create(Branch branch);
    List<BranchEntity> getBranchesByCompany(UUID companyId);
}

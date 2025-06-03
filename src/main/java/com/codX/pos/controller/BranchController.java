package com.codX.pos.controller;

import com.codX.pos.dto.Branch;
import com.codX.pos.entity.BranchEntity;
import com.codX.pos.service.BranchService;
import com.codX.pos.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/branch")
@CrossOrigin(origins = "http://localhost:3000")
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<StandardResponse> create(@RequestBody Branch branch) {
        BranchEntity createdBranch = branchService.create(branch);
        return new ResponseEntity<>(
                new StandardResponse(201, createdBranch, "Branch Created Successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<StandardResponse> getBranchesByCompany(@PathVariable UUID companyId) {
        List<BranchEntity> branches = branchService.getBranchesByCompany(companyId);
        return new ResponseEntity<>(
                new StandardResponse(200, branches, "Branches retrieved successfully"),
                HttpStatus.OK
        );
    }
}

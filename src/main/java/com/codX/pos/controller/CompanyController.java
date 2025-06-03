package com.codX.pos.controller;

import com.codX.pos.dto.Company;
import com.codX.pos.entity.CompanyEntity;
import com.codX.pos.service.CompanyService;
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
@RequestMapping("/api/v1/company")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Company company) {
        CompanyEntity createdCompany = companyService.create(company);
        return new ResponseEntity<>(
                new StandardResponse(201, createdCompany, "Company Created Successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllCompanies() {
        List<CompanyEntity> companies = companyService.getAllActiveCompanies();
        return new ResponseEntity<>(
                new StandardResponse(200, companies, "Companies retrieved successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<?> getCompanyById(@PathVariable UUID id) {
        CompanyEntity company = companyService.getCompanyById(id);
        return new ResponseEntity<>(
                new StandardResponse(200, company, "Company retrieved successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<?> updateCompany(@PathVariable UUID id, @RequestBody Company company) {
        CompanyEntity updatedCompany = companyService.updateCompany(id, company);
        return new ResponseEntity<>(
                new StandardResponse(200, updatedCompany, "Company updated successfully"),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}/max-branches")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateMaxBranches(@PathVariable UUID id, @RequestParam int maxBranches) {
        CompanyEntity company = companyService.updateMaxBranches(id, maxBranches);
        return new ResponseEntity<>(
                new StandardResponse(200, company, "Branch limit updated successfully"),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deactivateCompany(@PathVariable UUID id) {
        companyService.deactivateCompany(id);
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Company deactivated successfully"),
                HttpStatus.OK
        );
    }
}

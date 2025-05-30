package com.codX.pos.service.impl;

import com.codX.pos.dto.Company;
import com.codX.pos.entity.CompanyEntity;
import com.codX.pos.repository.CompanyRepository;
import com.codX.pos.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CompanyEntity create(Company company) {
        return companyRepository.save(objectMapper.convertValue(company, CompanyEntity.class));
    }
}

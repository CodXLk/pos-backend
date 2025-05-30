package com.codX.pos.service;

import com.codX.pos.dto.Company;
import com.codX.pos.entity.CompanyEntity;

public interface CompanyService {
    CompanyEntity create(Company company);
}

package com.codX.pos.controller;

import com.codX.pos.dto.Company;
import com.codX.pos.service.CompanyService;
import com.codX.pos.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> cerate(@RequestBody Company company){
        return new ResponseEntity<>(new StandardResponse(201,companyService.create(company),"Company Created Successfully"), HttpStatus.CREATED);
    }
}

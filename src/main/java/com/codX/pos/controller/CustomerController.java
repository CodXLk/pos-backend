package com.codX.pos.controller;

import com.codX.pos.auth.AuthenticationRequest;
import com.codX.pos.auth.AuthenticationResponse;
import com.codX.pos.auth.AuthenticationService;
import com.codX.pos.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> customerLogin(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticateCustomer(request);
        return new ResponseEntity<>(
                new StandardResponse(200, response, "Customer logged in successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/service-history")
    public ResponseEntity<?> getServiceHistory() {
        // TODO: Implement service history retrieval
        return new ResponseEntity<>(
                new StandardResponse(200, null, "Service history retrieved successfully"),
                HttpStatus.OK
        );
    }
}

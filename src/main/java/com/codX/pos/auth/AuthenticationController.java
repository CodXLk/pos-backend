package com.codX.pos.auth;

import com.codX.pos.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<StandardResponse> register(
            @RequestBody RegisterRequest request
    ){
        return new ResponseEntity<>(new StandardResponse(201,authenticationService.register(request),"User Created Successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<StandardResponse>register(
            @RequestBody AuthenticationRequest authenticationRequest
    ){
        return new ResponseEntity<>(new StandardResponse(200,authenticationService.authenticate(authenticationRequest),"User logged Successfully"), HttpStatus.OK);
    }
}

package com.codX.pos.controller;

import com.codX.pos.dto.User;
import com.codX.pos.service.UserService;
import com.codX.pos.util.StandardResponse;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> cerate(@RequestBody User user){
        return new ResponseEntity<>(new StandardResponse(201,userService.create(user),"User Created Successfully"), HttpStatus.CREATED);
    }

}

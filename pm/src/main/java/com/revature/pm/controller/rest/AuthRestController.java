package com.revature.pm.controller.rest;

import org.springframework.web.bind.annotation.*;

import com.revature.pm.dto.LoginDTO;
import com.revature.pm.dto.RegistrationDTO;
import com.revature.pm.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegistrationDTO dto) {
        authService.register(dto);
        return "User Registered Successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {
        return authService.login(dto);
    }
}
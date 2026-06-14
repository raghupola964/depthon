package com.depthon.controller;

import com.depthon.dto.RegisterRequest;
import com.depthon.model.User;
import com.depthon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.depthon.dto.LoginRequest;
import com.depthon.service.JwtService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        // Check the password (throws if wrong)
        userService.login(request);

        // Password is correct → make a token for this email
        String token = jwtService.generateToken(request.getEmail());

        // Send the token back
        return ResponseEntity.ok(Map.of("token", token));
    }
}
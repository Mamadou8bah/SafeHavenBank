package com.mamadou.safehavenbank.controller;

import com.mamadou.safehavenbank.dto.APIResponse;
import com.mamadou.safehavenbank.dto.LoginRequest;
import com.mamadou.safehavenbank.dto.LoginResponse;
import com.mamadou.safehavenbank.dto.RegisterRequest;
import com.mamadou.safehavenbank.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {

        try {
            authService.registerUser(registerRequest);
            return new ResponseEntity<>(APIResponse.success("User registered successfully,Please check your email to verify your account"), HttpStatus.OK);

        } catch (MessagingException | UnsupportedEncodingException e) {
            return new ResponseEntity<>(APIResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<APIResponse<Object>> verify(@RequestParam String token) {
        try{
            authService.verifyEmail(token);
            return new ResponseEntity<>(APIResponse.success("User verified successfully"), HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(APIResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try{
            LoginResponse loginResponse = authService.login(loginRequest);
            return new ResponseEntity<>(APIResponse.success("Login successfully",loginResponse), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(APIResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

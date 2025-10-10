package com.mamadou.safehavenbank.service;


import com.mamadou.safehavenbank.dto.LoginRequest;
import com.mamadou.safehavenbank.dto.LoginResponse;
import com.mamadou.safehavenbank.dto.RegisterRequest;
import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.enums.Role;
import com.mamadou.safehavenbank.exception.UnverifiedUserException;
import com.mamadou.safehavenbank.exception.UserAlreadyFoundException;
import com.mamadou.safehavenbank.repository.UserRepository;
import com.mamadou.safehavenbank.token.Token;
import com.mamadou.safehavenbank.token.TokenService;
import com.mamadou.safehavenbank.token.VerificationToken;
import com.mamadou.safehavenbank.token.VerificationTokenService;
import com.mamadou.safehavenbank.util.JwtUtil;
import com.mamadou.safehavenbank.wrapper.RegisterRequestWrapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    private final EmailService emailService;

    private final VerificationTokenService verificationTokenService;

    private final AuthenticationManager authenticationManager;

    private final TokenService  tokenService;

    private final JwtUtil jwtUtil;

    @Transactional
    public String register( @Valid RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            throw new UserAlreadyFoundException("There is an User with this email already!");
        }
        User userEntity = RegisterRequestWrapper.wrapToUser(registerRequest);
        userEntity.setPassword(encoder.encode(password));
        userEntity.setVerified(false);
        userEntity.setRole(Role.USER);
        userRepository.save(userEntity);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(userEntity);
        emailService.sendVerificationEmail(userEntity.getEmail(),verificationToken.getToken(), userEntity.getFullName() );

        return "Check Your Email to verify your account";
    }


    public LoginResponse login(@Valid LoginRequest loginRequest)  {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        Optional<User>user = userRepository.findByEmail(loginRequest.getEmail());
        User userEntity = user.orElseThrow(() -> new RuntimeException("User not found after authentication"));

        if (!userEntity.isVerified()) {
            throw new UnverifiedUserException("User is not verified");
        }

        Token refreshToken = tokenService.createToken(userEntity);
        String accessToken = jwtUtil.generateToken(userEntity);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccess_token(accessToken);
        loginResponse.setRefresh_token(refreshToken.getToken());
        return loginResponse;

    }

    public String verifyEmail(String token) {
        VerificationToken token1=verificationTokenService.findVerificationTokenByToken(token);
        if (token1 == null) {
            throw new RuntimeException("Verification Token not found");
        }
        User userEntity = token1.getUser();
        userEntity.setVerified(true);
        userRepository.save(userEntity);
        return "Email Verified! Please login";
    }

}

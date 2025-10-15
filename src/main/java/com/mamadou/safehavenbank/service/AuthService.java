package com.mamadou.safehavenbank.service;


import com.mamadou.safehavenbank.dto.LoginRequest;
import com.mamadou.safehavenbank.dto.LoginResponse;
import com.mamadou.safehavenbank.dto.PasswordResetRequest;
import com.mamadou.safehavenbank.dto.RegisterRequest;
import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.enums.Role;
import com.mamadou.safehavenbank.exception.TokenNotFoundException;
import com.mamadou.safehavenbank.exception.UnverifiedUserException;
import com.mamadou.safehavenbank.exception.UserAlreadyFoundException;
import com.mamadou.safehavenbank.exception.UserNotFoundException;
import com.mamadou.safehavenbank.repository.UserRepository;
import com.mamadou.safehavenbank.token.*;
import com.mamadou.safehavenbank.util.JwtUtil;
import com.mamadou.safehavenbank.wrapper.RegisterRequestWrapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
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

    private final PasswordResetTokenService  passwordResetTokenService;

    @Transactional
    public String registerUser( @Valid RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
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
        Optional<User>user = userRepository.findByEmailIgnoreCase(loginRequest.getEmail());
        User userEntity = user.orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!userEntity.isVerified()) {
            throw new UnverifiedUserException("User is not verified");
        }

        userEntity.getTokens().stream().forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });

        Token refreshToken = tokenService.createToken(userEntity);
        String accessToken = jwtUtil.generateToken(userEntity);

        userRepository.save(userEntity);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccess_token(accessToken);
        loginResponse.setRefresh_token(refreshToken.getToken());
        return loginResponse;

    }

    public String resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        String cleanedEmail = email == null ? null : email.trim();
        User user = userRepository.findByEmailIgnoreCase(cleanedEmail).orElseThrow(() -> new UserNotFoundException("User not found please register first"));

        if (user.isVerified()) {
            return "Your account is already verified.";
        }
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(),verificationToken.getToken(), user.getFullName() );

        return "Check Your Email again to verify your account";
    }

    public String verifyEmail(String token) {
        VerificationToken token1=verificationTokenService.findVerificationTokenByToken(token);
        if (token1 == null) {
            throw new TokenNotFoundException("Token not found");
        }
        User userEntity = token1.getUser();
        userEntity.setVerified(true);
        userRepository.save(userEntity);
        return "Email Verified! Please login";
    }

    public String logout() {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        User userEntity = user.orElseThrow(() -> new UserNotFoundException("User not found after authentication"));
        userEntity.getTokens().stream()
                .filter(token->
                    !token.isExpired() && !token.isRevoked()
                )
                .forEach((token)->{
            token.setExpired(true);
            token.setRevoked(true);
        });
        userRepository.save(userEntity);
        SecurityContextHolder.getContext().setAuthentication(null);
        return "Logged out";
    }

    @Transactional
    public LoginResponse refreshToken(String token) {

        String email = jwtUtil.extractUsername(token);

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        User userEntity = user.orElseThrow(() -> new UserNotFoundException("User not found after authentication"));

        boolean isValid= jwtUtil.isTokenValid(token, userEntity);

        if (!isValid) {
            throw new TokenNotFoundException("Token not Valid");
        }
        String access_token = jwtUtil.generateToken(userEntity);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccess_token(access_token);
        loginResponse.setRefresh_token(token);
        return loginResponse;
    }

    @Transactional
    public String requestPasswordReset(String email) throws MessagingException, UnsupportedEncodingException {
        Optional<User>user = userRepository.findByEmailIgnoreCase(email);
        User userEntity = user.orElseThrow(() -> new UserNotFoundException("User not found"));
        PasswordResetToken passwordResetToken=passwordResetTokenService.createPasswordResetToken(userEntity);

        emailService.sendPasswordResetEmail(email, passwordResetToken.getToken(), userEntity.getFullName());

        return "Check Your Email to reset your password";
    }

    @Transactional
    public String resetPassword(@Valid PasswordResetRequest request){
        String token=request.getToken();
        String password=request.getPassword();

        var user=passwordResetTokenService.getUserByToken(token);

        if(user==null){
            throw new UserNotFoundException("User not found");
        }
        user.setPassword(encoder.encode(password));

        user.getTokens().stream().forEach(token1->{
            token1.setExpired(true);
            token1.setRevoked(true);
        });
        userRepository.save(user);
        return "Password Reset Successful";
    }

    public List<User>getAllUsers(){
        return userRepository.findAll();
    }






}

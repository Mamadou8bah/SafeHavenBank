package com.mamadou.safehavenbank.service;


import com.mamadou.safehavenbank.dto.RegisterRequest;
import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.exception.UserAlreadyFoundException;
import com.mamadou.safehavenbank.repository.UserRepository;
import com.mamadou.safehavenbank.token.VerificationToken;
import com.mamadou.safehavenbank.token.VerificationTokenService;
import com.mamadou.safehavenbank.wrapper.RegisterRequestWrapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private  final BCryptPasswordEncoder encoder;

    private final EmailService emailService;

    private final VerificationTokenService verificationTokenService;

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
        userRepository.save(userEntity);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(userEntity);
        emailService.sendVerificationEmail(userEntity.getEmail(),verificationToken.getToken(), userEntity.getFullName() );

        return "Check Your Email to verify your account";
    }


}

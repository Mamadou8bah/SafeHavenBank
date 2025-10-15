package com.mamadou.safehavenbank.token;

import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken();
        String token = UUID.randomUUID().toString();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
    public VerificationToken findVerificationTokenByToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token doesn't exist"));
    }

}
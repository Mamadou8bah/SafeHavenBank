package com.mamadou.safehavenbank.token;

import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository repository;

    public PasswordResetToken createPasswordResetToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(generate6DigitToken());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setExpired(false);
        repository.save(token);
        return token;
    }

    public PasswordResetToken findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    public void invalidateToken(PasswordResetToken token) {
        token.setExpired(true);
        repository.save(token);
    }

    private String generate6DigitToken() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    public User getUserByToken(String token) {
        return repository.findByToken(token)
                .map(
                    PasswordResetToken::getUser
                ).orElse(null);
    }
}

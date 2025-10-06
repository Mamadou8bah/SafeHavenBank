package com.mamadou.safehavenbank.token;

import com.mamadou.safehavenbank.entity.User;
import com.mamadou.safehavenbank.repository.TokenRepository;
import com.mamadou.safehavenbank.repository.UserRepository;
import com.mamadou.safehavenbank.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Transactional
    public Token createToken(String token) {
        String email = jwtUtil.extractEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

        Token tokenObj = new Token();
        tokenObj.setToken(token);
        tokenObj.setExpired(false);
        tokenObj.setRevoked(false);
        tokenObj.setUser(user);

        return tokenRepository.save(tokenObj);
    }
    
    

}

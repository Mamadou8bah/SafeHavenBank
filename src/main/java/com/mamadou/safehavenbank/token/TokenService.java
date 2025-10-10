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
    public Token createToken(User user) {
       var jwtToken=jwtUtil.generateRefreshToken(user);
       Token token1=new Token();
       token1.setToken(jwtToken);
       token1.setUser(user);
       token1.setRevoked(false);
       token1.setExpired(false);
       return tokenRepository.save(token1);
    }
    
    

}

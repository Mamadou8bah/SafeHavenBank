package com.mamadou.safehavenbank.util;

import com.mamadou.safehavenbank.repository.TokenRepository;
import com.mamadou.safehavenbank.token.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@SuppressWarnings("ALL")
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private final TokenRepository tokenRepository;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .subject(email)
                .signWith(getKey())
                .compact();
    }

    public String extractEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {

            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(getKey())
                    .build()
                    .parseSignedClaims(token);


            Date expirationDate = claims.getBody().getExpiration();
            if (expirationDate.before(new Date())) {
                return false;
            }
            Optional<Token> optionalToken = tokenRepository.findByToken(token);
            if (optionalToken.isEmpty()) {
                return false;
            }

            Token storedToken = optionalToken.get();
            return !storedToken.isExpired() && !storedToken.isRevoked();

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

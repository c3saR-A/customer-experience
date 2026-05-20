package com.project.customer_experience.services;

import com.project.customer_experience.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secret}") String secretKey;

    private final long ACCESS_TOKEN_EXP = 10000 * 60 * 15; // 15 min
    private final long REFRESH_TOKEN_EXP = 1000 * 60 * 60 * 24 * 7; // 7 d

    public String generateAccessToken(Authentication auth) {
        return createToken(auth, ACCESS_TOKEN_EXP);
    }

    public String generateRefreshToken(Authentication auth) {
        return createToken(auth, REFRESH_TOKEN_EXP);
    }

    private String createToken(Authentication auth, long expirationTime) {
        User user = (User) auth.getPrincipal(); // Asumiendo que tu entidad User implementa UserDetails

        System.out.println(secretKey);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .claim("roles", auth.getAuthorities()) // Útil para el @PreAuthorize
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    //
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
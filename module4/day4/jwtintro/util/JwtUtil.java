package org.example.jwtintro.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // Must be at least 256 bits (32 characters) long
    private static final String SECRET_STRING = "your-super-secret-key-that-is-at-least-32-characters-long!";
    private final SecretKey SEC_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 Hour

    public String generateToken(String username){
        Map<String,Object> additionalClaims=new HashMap<>();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claims(additionalClaims)
                .signWith(SEC_KEY)
                .compact()
                ;

    }
    private Claims extractAllClaims(String token){
        return  Jwts.parser()
                .verifyWith(SEC_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String extractUserName(String token){
        Claims claims=extractAllClaims(token);
        return claims.getSubject();
    }
    public Date extractExpiryDate(String token){
        Claims claims=extractAllClaims(token);
        return claims.getExpiration();
    }
    public Date extractIssueDate(String token){
        Claims claims=extractAllClaims(token);
        return claims.getIssuedAt();
    }

    public boolean isTokenExpired(String token){
        return extractExpiryDate(token).before(new Date());
    }
    public boolean validateToken(String token, UserDetails userDetails){
        final String username=extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }



}

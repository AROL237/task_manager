package com.cova.core.service;


import com.cova.core.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class JwtService {


    @Value("${JWT_SECRET_HASH}")
    private String jwtSecretHash;

    @Value("${JWT_EXPIRY_TIME}")
    private String jwtExpiryTime;

    private SecretKey getSecretKey() {
        byte[] encodedKey = Decoders.BASE64.decode(jwtSecretHash);

        return Keys.hmacShaKeyFor(encodedKey);
    }

    public List<String> getAuthoritiesAsString(User user) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) user.getAuthorities();

        return authorities.stream().map(GrantedAuthority::getAuthority).toList();
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date exp = Date.from(Instant.now().plus(Integer.parseInt(jwtExpiryTime), ChronoUnit.DAYS));

        Map<String, List<String>> claims = new HashMap<>();
        claims.put("roles", getAuthoritiesAsString(user));

        return Jwts.builder()
                .subject(user.getEmail().trim())
                .issuer("aiba-pay")
                .claims(claims)
                .issuedAt(new Date())
                .expiration(exp)
                .signWith(getSecretKey())
                .compact();
    }

    public Collection<? extends GrantedAuthority> getClaims(String token) {
        List<String> roles = (List<String>) Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", List.class);

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

   public boolean isTokenExpired(String token) {
        Date exp = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return exp.before(new Date());
    }

   public boolean isTokenValid(String token, User user) {

        return extractUsername(token).equals(user.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}

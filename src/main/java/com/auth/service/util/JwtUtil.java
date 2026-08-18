package com.auth.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    }

    public String generateToken(String username, java.util.Set<String> roles, java.util.Set<String> permissions) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("roles", String.join(",", roles));
        if (permissions != null && !permissions.isEmpty()) {
            builder.claim("permissions", String.join(",", permissions));
        }
        return builder.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public java.util.Set<String> extractRoles(String token) {
        Claims claims = getClaims(token);
        String rolesCsv = claims.get("roles", String.class);
        if (rolesCsv == null || rolesCsv.isBlank()) return java.util.Collections.emptySet();
        String[] parts = rolesCsv.split(",");
        return java.util.Arrays.stream(parts).map(String::trim).collect(java.util.stream.Collectors.toSet());
    }

    public java.util.Set<String> extractPermissions(String token) {
        Claims claims = getClaims(token);
        String permsCsv = claims.get("permissions", String.class);
        if (permsCsv == null || permsCsv.isBlank()) return java.util.Collections.emptySet();
        String[] parts = permsCsv.split(",");
        return java.util.Arrays.stream(parts).map(String::trim).collect(java.util.stream.Collectors.toSet());
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }
}

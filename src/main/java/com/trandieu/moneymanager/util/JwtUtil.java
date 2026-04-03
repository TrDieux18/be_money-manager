package com.trandieu.moneymanager.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

   @Value("${app.jwt.secret}")
   private String jwtSecret;

   @Value("${app.jwt.expiration-ms}")
   private long jwtExpirationMs;

   public String generateToken(String email) {
      Date now = new Date();
      Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

      return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
   }

   public String extractEmail(String token) {
      return extractAllClaims(token).getSubject();
   }

   public boolean isTokenValid(String token, String email) {
      String tokenEmail = extractEmail(token);
      return tokenEmail.equals(email) && !isTokenExpired(token);
   }

   private boolean isTokenExpired(String token) {
      return extractAllClaims(token).getExpiration().before(new Date());
   }

   private Claims extractAllClaims(String token) {
      return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
   }

   private Key getSigningKey() {
      return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
   }
}

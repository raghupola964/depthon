package com.depthon.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Turn the secret string into a cryptographic key object
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // CREATE a token for a user (called at login)
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)                 // the "sub" claim — who this token is for
                .issuedAt(now)                  // when it was made
                .expiration(expiryDate)         // when it dies (24h)
                .signWith(getSigningKey())      // sign it with our secret → the signature
                .compact();                     // squash into the final string
    }

    // READ the email out of a token (and verify it in the process)
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())    // check the signature with our secret
                .build()
                .parseSignedClaims(token)       // throws if tampered/expired/fake
                .getPayload();
        return claims.getSubject();             // pull out the "sub" → the email
    }

    // Is this token genuine AND not expired?
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);  // if this doesn't throw, it's valid
            return true;
        } catch (Exception e) {
            return false;                       // tampered, expired, or garbage
        }
    }
}
package com.stamina_inc.academiq_auth.utils;

import com.stamina_inc.academiq_auth.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class JwtUtils {

    public static final long EXPIRATION_TIME = 86400000;
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Generate a JWT token for the given user details
     *
     * @param customUserDetails The user details to generate the token for
     * @return The generated JWT token
     */
    public String generateToken(CustomUserDetails customUserDetails) {
        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = Jwts.builder()
                .subject(customUserDetails.getUsername())
                .claim("id", customUserDetails.getId())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();

        return String.format("Bearer %s", token);
    }

    /**
     * Extract claims from the given JWT token
     *
     * @param token The JWT token to extract claims from
     * @return The extracted claims
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = getSigningKey();

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validate the given JWT token
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        SecretKey key = getSigningKey();

        JwtParser parser = Jwts.parser()
                .verifyWith(key)
                .build();

        try {
            parser.parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract the actual token from the given Bearer token string
     *
     * @param token
     * @return
     * @throws MalformedJwtException
     */
    public String extractToken(String token) throws MalformedJwtException {
        Objects.requireNonNull(token);

        if (token.isBlank() || !token.startsWith("Bearer ")) {
            throw new MalformedJwtException("Invalid token");
        }

        // Extract the actual token part (after "Bearer")
        return token.substring(7);
    }

    /**
     * Get the signing key from the JWT secret
     *
     * @return The signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
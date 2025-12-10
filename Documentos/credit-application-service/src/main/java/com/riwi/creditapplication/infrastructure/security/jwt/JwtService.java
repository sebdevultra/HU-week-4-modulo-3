package com.riwi.creditapplication.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT Service for token generation and validation.
 * Handles all JWT operations in a stateless manner.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Extract username from JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a specific claim from token.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract the claim
     * @param <T>            type of the claim
     * @return the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate JWT token for user.
     *
     * @param userDetails the user details
     * @return the generated token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate JWT token with extra claims.
     *
     * @param extraClaims additional claims to include
     * @param userDetails the user details
     * @return the generated token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Generate refresh token.
     *
     * @param userDetails the user details
     * @return the refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Build JWT token.
     *
     * @param extraClaims additional claims
     * @param userDetails user details
     * @param expiration  token expiration time
     * @return the JWT token
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        // Add roles to claims
        var authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        extraClaims.put("roles", authorities);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate if token is valid for user.
     *
     * @param token       the JWT token
     * @param userDetails the user details
     * @return true if valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Check if token is expired.
     *
     * @param token the JWT token
     * @return true if expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date from token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract all claims from token.
     *
     * @param token the JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get signing key for JWT.
     *
     * @return the signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract roles from token.
     *
     * @param token the JWT token
     * @return list of roles
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return (java.util.List<String>) claims.get("roles");
    }

    /**
     * Get token expiration time in milliseconds.
     *
     * @return expiration time
     */
    public long getJwtExpiration() {
        return jwtExpiration;
    }

    /**
     * Get refresh token expiration time in milliseconds.
     *
     * @return refresh expiration time
     */
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}

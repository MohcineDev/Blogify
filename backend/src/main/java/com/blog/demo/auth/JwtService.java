package com.blog.demo.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.blog.demo.users.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${ACCESS_TOKEN_SECRET}")
    private String ACCESS_TOKEN_SECRET;
    @Value("${JWT_EXP}")
    private long JWT_EXP;

    public String generateToken(User user) {
        //pieces of info
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        return createToken(claims, user.getUsername());
    }

    public String generateToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expire = new Date(System.currentTimeMillis() + JWT_EXP);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /// get username from token
    public String extractUsername(String token) {
        Claims allClaims = extractAllClaims(token);
        String username = allClaims.getSubject();
        // return extractClaim(token, Claims::getSubject);
        return username;
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    // validate token
    public boolean isTokenValid(String token, User userDetails) {
        String username = extractUsername(token);
        Long userId = extractUserId(token);
        return (username.equals(userDetails.getUsername())) && (userId.equals(userDetails.getId())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey() {

        if (ACCESS_TOKEN_SECRET == null || ACCESS_TOKEN_SECRET.isEmpty()) {

            System.err.println(" MISSING SECRET KEY OR EMPTY!");
            throw new RuntimeException("MISSING SECRET KEY OR EMPTY");
        }

        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_TOKEN_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

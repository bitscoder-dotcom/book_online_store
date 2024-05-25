package com.bitscoder.onlinebookstore.security.jwt;

import com.bitscoder.onlinebookstore.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * The JwtUtils class is a component that handles operations related to JSON Web Tokens (JWT).
 * It reads the JWT expiration time and secret key from the application properties.
 * The init method initializes the secret key for HMAC SHA.
 * The generateJwtToken method generates a JWT token for an authenticated user.
 * The getUserNameFromJwtToken method extracts the username from a given JWT token.
 * The validateJwtToken method validates a given JWT token and logs different errors for various exceptions.
 * The getJwtExpirationMs method returns the JWT expiration time in milliseconds.
 * The getJwtExpirationDate method calculates and returns the expiration date of the JWT.
 */

@Component
@Slf4j
public class JwtUtils {

    @Value("${lms.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${lms.jwtSecretKey}")
    private String jwtSecretKey;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }


    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("'Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public LocalDateTime getJwtExpirationDate() {
        return Instant.ofEpochMilli((new Date()).getTime() + getJwtExpirationMs())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}

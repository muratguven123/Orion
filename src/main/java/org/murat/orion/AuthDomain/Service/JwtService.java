package org.murat.orion.AuthDomain.Service;

import io.jsonwebtoken.Jwts;
import org.murat.orion.AuthDomain.Entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKey12345678901234567890123456789012}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    public String generateToken(User user) {
        return buildToken(new HashMap<>(), user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}

package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        byte [] keyBytes= secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims extractAllClaims(String token){
        return io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaim(String token, java.util.function.Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
        public boolean isTokenExpired(String token) {
            return extractClaim(token, Claims::getExpiration).before(new java.util.Date());
        }
        public void validateToken(final String token) {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
        }
        public String extractUserId(String token) {
            return extractClaim(token, Claims::getSubject);
        }
}

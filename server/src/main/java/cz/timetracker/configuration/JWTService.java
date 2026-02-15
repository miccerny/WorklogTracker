package cz.timetracker.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecretJwk;
import lombok.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Generates JWT access tokens for authenticated users.
 *
 * Note:
 * Uses JJWT builder fluent API (subject/issuedAt/expiration),
 * because older setter-based methods are deprecated in newer JJWT versions.
 */
@Service
public class JWTService {

    /** Token validity duration (e.g., 1 hour). */
    private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(1);

    private final Key signingKey;

    public JWTService(@Value("${security.jwt.secret}") String secret) {
        // Important: for HS256 the secret must be sufficiently long (>= 256 bits => 32+ bytes).
        // Using UTF-8 to avoid platform-dependent charset issues.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates a signed JWT token for the given user.
     */
    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(JWTService.ACCESS_TOKEN_TTL);

        return Jwts.builder()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .id(UUID.randomUUID().toString()) // jti: helps with token tracking/revocation lists
                .signWith(signingKey, SignatureAlgorithm.HS256) // explicit algorithm (stable + clear)
                .compact();

    }
}

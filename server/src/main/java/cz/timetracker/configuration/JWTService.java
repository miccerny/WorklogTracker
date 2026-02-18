package cz.timetracker.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Service responsible for creating and validating JWT tokens.
 *
 * <p>This class focuses on:</p>
 * <ul>
 *     <li>generating JWT tokens after successful login,</li>
 *     <li>extracting the username (subject) from a token,</li>
 *     <li>validating token signature and expiration.</li>
 * </ul>
 *
 * <p><b>Beginner note:</b> JWT is a signed string containing claims (data).
 * The server verifies the signature using the secret key. If the signature is valid
 * and the token is not expired, we can trust its content.</p>
 */
@Service
public class JWTService {

    /**
     * How long an access token is valid.
     */
   private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(1);

    /**
     * Secret key used to sign and validate tokens.
     *
     * <p>Note: Keep this secret safe (do not commit it to GitHub).
     * In production, store it in environment variables or a secrets manager.</p>
     */
   private final SecretKey signingKey;

    /**
     * Creates the JWT service and prepares the signing key.
     *
     * @param secret raw secret read from application properties
     */
   public JWTService(@Value("${security.jwt.secret}") String secret){
       this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
   }

    /**
     * Generates a signed JWT token for the given user.
     *
     * <p>Important standard claims used here:</p>
     * <ul>
     *     <li>{@code sub} (subject) - the username/email</li>
     *     <li>{@code iat} (issued at) - when the token was created</li>
     *     <li>{@code exp} (expiration) - when the token will expire</li>
     *     <li>{@code jti} (JWT ID) - unique id of the token</li>
     * </ul>
     *
     * @param user authenticated user
     * @return signed JWT token as a string
     */
   public String generateToken(UserDetails user){
       Instant now = Instant.now();
       Instant expiresAt = now.plus(ACCESS_TOKEN_TTL);

       return Jwts.builder()
               // subject = main identifier (email/username)
               .subject(user.getUsername())
               // iat = token creation time
               .issuedAt(Date.from(now))
               // exp = token expiration time
               .expiration(Date.from(expiresAt))
               // jti = unique token id (useful for debugging/revocation lists)
               .id(UUID.randomUUID().toString())
               // Sign the token with HMAC SHA-256.
               .signWith(signingKey, SignatureAlgorithm.HS256)
               .compact();
   }

    /**
     * Extracts the username (subject) from the token.
     *
     * @param token raw JWT token (without "Bearer " prefix)
     * @return username stored in the {@code sub} claim
     */
   public String extractUsername(String token){
       return extractAllClaims(token).getSubject();
   }

    /**
     * Checks whether a token belongs to the given user and is not expired.
     *
     * <p>Beginner note: this is a minimal validation. In more advanced setups,
     * you may also validate issuer, audience, roles/claims, and support token revocation.</p>
     *
     * @param token raw JWT token
     * @param userDetails user loaded from the database
     * @return {@code true} if token is valid for the user
     */
   public boolean isTokenValid(String token, UserDetails userDetails){
       String username = extractUsername(token);
       return username
               .equals(userDetails
                       .getUsername()) && !isTokenExpired(token);
   }

    /**
     * Checks whether the token is expired.
     *
     * @param token raw JWT token
     * @return {@code true} if token expiration time is in the past
     */
   private boolean isTokenExpired(String token){
       return extractAllClaims(token).getExpiration()
               .before(new Date());
   }

    /**
     * Parses the token, verifies its signature, and returns all claims.
     *
     * <p>Beginner note: If the token signature is invalid or the token is malformed,
     * JJWT will throw an exception.</p>
     *
     * @param token raw JWT token
     * @return parsed {@link Claims}
     */
   private Claims extractAllClaims(String token){
       return Jwts.parser()
               .verifyWith(signingKey)
               .build()
               .parseSignedClaims(token)
               .getPayload();
   }


}

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
 * Jednoduchá servisní třída pro práci s JWT tokeny.
 *
 * <p>Co tady řešíme:
 * <ul>
 *     <li>vytvoření tokenu po úspěšném přihlášení,</li>
 *     <li>získání username (subjectu) z tokenu,</li>
 *     <li>ověření, že token patří danému uživateli a neexpiroval.</li>
 * </ul>
 */
@Service
public class JWTService {

    /**
     * Jak dlouho bude access token platit.
     */
   private static final Duration ACCES_TOKEN_TTL = Duration.ofHours(1);

    /**
     * Tajný klíč pro podepisování a validaci tokenů.
     */
   private final SecretKey signingKey;


   public JWTService(@Value("${security.jwt.secret}") String secret){
       this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
   }

    /**
     * Vygeneruje JWT token pro přihlášeného uživatele.
     *
     * @param user přihlášený uživatel
     * @return podepsaný JWT string
     */
   public String generateToken(UserDetails user){
       Instant now = Instant.now();
       Instant expiratesAt = now.plus(ACCES_TOKEN_TTL);

       return Jwts.builder()
               // subject = hlavní identifikátor uživatele (u nás email/username)
               .subject(user.getUsername())
               // iat = čas vystavení tokenu
               .issuedAt(Date.from(now))
               // exp = čas expirace
               .expiration(Date.from(expiratesAt))
               // jti = unikátní ID tokenu
               .id(UUID.randomUUID().toString())
               // podepsání tokenu klíčem + HS256
               .signWith(signingKey, SignatureAlgorithm.HS256)
               .compact();
   }

    /**
     * Vytáhne username (subject) z tokenu.
     *
     * @param token JWT token z Authorization headeru
     * @return username uložený v subject claimu
     */
   public String extractUsername(String token){
       return extractAllClaims(token).getSubject();
   }

    /**
     * Ověří, že token odpovídá konkrétnímu uživateli a neexpiroval.
     *
     * @param token JWT token
     * @param userDetails uživatel načtený z DB
     * @return true pokud je token validní
     */
   public boolean isTokenValid(String token, UserDetails userDetails){
       String username = extractUsername(token);
       return username
               .equals(userDetails
                       .getUsername()) && !isTokenExpired(token);
   }

    /**
     * Zjistí, zda token expiroval.
     */
   private boolean isTokenExpired(String token){
       return extractAllClaims(token).getExpiration()
               .before(new Date());
   }

    /**
     * Rozparsuje a ověří podpis tokenu, pak vrátí Claims.
     */
   private Claims extractAllClaims(String token){
       return Jwts.parser()
               .verifyWith(signingKey)
               .build()
               .parseSignedClaims(token)
               .getPayload();
   }


}

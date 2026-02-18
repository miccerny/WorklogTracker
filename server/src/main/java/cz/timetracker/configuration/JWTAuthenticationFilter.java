package cz.timetracker.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that runs once per request.
 *
 * <p>This filter checks the {@code Authorization} header for a {@code Bearer <token>} value.
 * If a token is present and valid, it creates an {@link UsernamePasswordAuthenticationToken}
 * and stores it in the {@link SecurityContextHolder} so controllers/services can access the
 * authenticated user.</p>
 *
 * <p><b>Beginner note:</b> This filter does not "log in" the user with a session. It only
 * sets authentication for the current request based on the JWT token.</p>
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Creates the filter with required dependencies.
     *
     * @param jwtService service responsible for JWT parsing and validation
     * @param userDetailsService service used to load the user from the database
     */
    public JWTAuthenticationFilter(JWTService jwtService,
                                   UserDetailsService userDetailsService){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Hlavní metoda filtru:
     * 1) přečte Authorization header,
     * 2) zkontroluje Bearer token,
     * 3) načte uživatele z DB,
     * 4) validuje token,
     * 5) uloží Authentication do SecurityContextHolder.
     */

    /**
     * Main filter method executed for each incoming HTTP request.
     *
     * <p>Steps:</p>
     * <ol>
     *     <li>Read {@code Authorization} header.</li>
     *     <li>If it does not start with {@code Bearer }, skip JWT logic.</li>
     *     <li>Extract username from token.</li>
     *     <li>Load user details from DB.</li>
     *     <li>Validate the token.</li>
     *     <li>If valid, put Authentication into {@link SecurityContextHolder}.</li>
     * </ol>
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param filterChain chain of remaining filters
     * @throws ServletException when filter processing fails
     * @throws IOException when I/O fails
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException{
        String authorizationHeader = request.getHeader("Authorization");

        //note: If there's no token, we just continue. Public endpoints still work.
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " is 7 characters, so we cut it off to get the raw JWT.
        String jwt = authorizationHeader.substring(7);

        // Extract the username (subject) from the token.
        String username = jwtService.extractUsername(jwt);


        // Only authenticate if:
        // 1) we extracted a username, and
        // 2) the request is not already authenticated (prevents overwriting auth).
        if(username != null && SecurityContextHolder
                .getContext()
                .getAuthentication() == null){
            // Load the user from DB so we can verify token belongs to a real user.
            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(username);
            // Validate signature + expiration + that it matches the loaded user.
            if(jwtService.isTokenValid(jwt, userDetails)){
                // Create an Authentication object that Spring Security understands.
                UsernamePasswordAuthenticationToken
                        authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                        null, // no credentials stored here (password is not needed at this point)
                        userDetails.getAuthorities()
                );
                // Attach request details (e.g., IP, session id) to the authentication.
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Store it in the SecurityContext so downstream code can use it.
                SecurityContextHolder.getContext().setAuthentication(
                        authenticationToken
                );
            }
        }
        // Continue processing the request.
        filterChain.doFilter(request, response);
    }

}

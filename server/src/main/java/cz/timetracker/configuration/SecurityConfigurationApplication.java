package cz.timetracker.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurationApplication {

<<<<<<< Updated upstream
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
=======
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    /**
     * Constructor injection of custom JWT filter.
     *
     * @param jwtAuthenticationFilter filter responsible for validating JWT tokens
     */
    public SecurityConfigurationApplication(JWTAuthenticationFilter jwtAuthenticationFilter,
                                            UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }


    /**
     * Defines the main security filter chain.
     *
     * <p>This is where we configure:</p>
     * <ul>
     *     <li>CSRF behavior</li>
     *     <li>which endpoints are public</li>
     *     <li>session management strategy</li>
     *     <li>JWT filter registration</li>
     * </ul>
     *
     * @param httpSecurity Spring Security HTTP configuration
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // HttpSecurity is configured using a fluent API (method chaining).
        return httpSecurity
                // Enable CORS support. Spring will look for a CorsConfigurationSource bean (defined below).
                .cors(Customizer.withDefaults())

                // Disable CSRF because we use stateless JWT authentication.
                //Note: CSRF protection is mainly needed for session-based apps.
                .csrf(csrf -> csrf.disable())

                // Configure authorization rules for HTTP requests.
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints (e.g., login, registration).
                        // These do not require authentication.
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // All other endpoints require authentication.
                        .anyRequest().authenticated())

                // Stateless session management (important for JWT).
                // Beginner note: No HTTP session is created or stored on the server.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add our custom JWT filter before Spring's default authentication filter.
                // This ensures JWT is validated before accessing secured endpoints.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Finalize and create the filter chain instance used by Spring Security.
>>>>>>> Stashed changes
                .build();
    }

}

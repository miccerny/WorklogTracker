package cz.timetracker.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Main Spring Security configuration for the application.
 *
 * <p>This class defines:</p>
 * <ul>
 *     <li>which endpoints are public and which require authentication,</li>
 *     <li>how authentication is handled (JWT-based, stateless),</li>
 *     <li>which security filters are active,</li>
 *     <li>password encoding strategy.</li>
 * </ul>
 *
 * <p><b>Beginner note:</b> This class does NOT authenticate users directly.
 * It only configures how Spring Security should behave.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurationApplication {

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
     * @param http Spring Security HTTP configuration
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
                        .requestMatchers("/api/auth/**").permitAll()

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
                .build();
    }

    /**
     * Defines the password encoder used for hashing user passwords.
     *
     * <p>BCrypt is a strong one-way hashing algorithm recommended for password storage.</p>
     *
     * <p><b>Beginner note:</b> We NEVER store raw passwords in the database.
     * Only hashed values are stored.</p>
     *
     * @return {@link PasswordEncoder} implementation
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes {@link AuthenticationManager} as a Spring bean.
     *
     * <p>This is typically used in login endpoints to authenticate
     * username + password before generating a JWT.</p>
     *
     * @param configuration Spring authentication configuration
     * @return configured {@link AuthenticationManager}
     * @throws Exception if creation fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception{
        return configuration.getAuthenticationManager();

    }

    /**
     * Provides CORS configuration for the application.
     *
     * <p>CORS (Cross-Origin Resource Sharing) controls whether a browser frontend
     * (running on a different origin like http://localhost:5173) is allowed to call this backend.
     *
     * <p>This configuration:
     * <ul>
     *   <li>Allows a specific origin (Vite dev server)</li>
     *   <li>Allows common HTTP methods used by REST APIs</li>
     *   <li>Allows any request header</li>
     *   <li>Allows credentials (cookies/authorization headers) to be included</li>
     * </ul>
     *
     * @return {@link CorsConfigurationSource} used by Spring Security and Spring MVC
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins = which frontend URLs are allowed to call this backend from the browser.
        // In development, Vite runs commonly on 5173.
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173"
        ));

        // Allowed HTTP methods for cross-origin requests.
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Allowed headers that the frontend may send (Authorization, Content-Type, etc.).
        // "*" means any header.
        configuration.setAllowedHeaders(List.of("*"));

        // If true, browser is allowed to include credentials in CORS requests.
        // Example: cookies or Authorization headers (depending on your setup).
        configuration.setAllowCredentials(true);

        // Register this CORS config for all endpoints (/**).
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

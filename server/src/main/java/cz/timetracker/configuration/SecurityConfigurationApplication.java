package cz.timetracker.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
 * * Spring Security konfigurace aplikace.
 *
 * <p>Nastavuje stateless JWT přístup:
 * <ul>
 *   <li>/api/auth/** je veřejné (login/registrace),</li>
 *    <li>ostatní /api/** endpointy vyžadují validní JWT token,</li>
 *   <li>requesty jsou bez session (STATELESS).</li>
 *   * </ul>
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurationApplication {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    public SecurityConfigurationApplication(JWTAuthenticationFilter jwtAuthenticationFilter,
                                            UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }


    /**
     * Sestaví hlavní security filter chain.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // HttpSecurity is configured using a fluent API (method chaining).
        return httpSecurity
                // Enable CORS support. Spring will look for a CorsConfigurationSource bean (defined below).
                .cors(Customizer.withDefaults())

                // CSRF is mainly relevant for browser-based sessions with cookies.
                // For APIs (especially with tokens) it is often disabled, but it depends on your auth approach.
                .csrf(csrf -> csrf.disable())


                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Authorization rules: who can access which URL.
                .authorizeHttpRequests(auth -> auth

                        // Allow access to any endpoint under /api/**
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        // Allow access to everything else as well (very open setup).
                        // Later you might change this to authenticated() or define more matchers.
                        .anyRequest().permitAll())

                // Finalize and create the filter chain instance used by Spring Security.
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider
                authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
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

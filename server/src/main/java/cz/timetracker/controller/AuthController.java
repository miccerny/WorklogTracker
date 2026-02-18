package cz.timetracker.controller;

import cz.timetracker.dto.user.LoginRequest;
import cz.timetracker.dto.user.LoginResponse;
import cz.timetracker.dto.user.UserRegistryRequest;
import cz.timetracker.dto.user.UserResponse;
import cz.timetracker.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for authentication endpoints.
 *
 * <p>This controller exposes public endpoints for:</p>
 * <ul>
 *     <li>User registration</li>
 *     <li>User login (JWT token generation)</li>
 * </ul>
 *
 * <p><b>Beginner note:</b> These endpoints are usually configured as
 * {@code permitAll()} in {@code SecurityConfiguration} because
 * users must be able to register and log in without being authenticated.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor injection of authentication service.
     *
     * @param authService service handling registration and login logic
     */
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    /**
     * Registers a new user.
     *
     * <p>Returns HTTP 201 (Created) when successful.</p>
     *
     * @param request incoming registration request
     * @return created user as {@link UserResponse}
     */
    @PostMapping("/register")
    public UserResponse register(@RequestBody UserRegistryRequest request){
        return authService.createUser(request);
    }


    /**
     * Authenticates a user and returns a JWT access token.
     *
     * <p>Returns HTTP 200 (OK) when credentials are valid.</p>
     *
     * <p>If credentials are invalid, Spring Security will throw
     * {@code BadCredentialsException}, which should be handled
     * globally via {@code @ControllerAdvice}.</p>
     *
     * @param request login request containing username and password
     * @return login response containing user data + JWT token
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.loadUser(request);
    }
}

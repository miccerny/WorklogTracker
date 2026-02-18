package cz.timetracker.service;

import cz.timetracker.configuration.JWTService;
import cz.timetracker.dto.user.LoginRequest;
import cz.timetracker.dto.user.LoginResponse;
import cz.timetracker.dto.user.UserRegistryRequest;
import cz.timetracker.dto.mapper.UserMapper;
import cz.timetracker.dto.user.UserResponse;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.service.exceptions.DuplicateEmailException;
import cz.timetracker.service.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication/registration service implementation.
 *
 * <p>This service provides:</p>
 * <ul>
 *     <li>User registration (create new user with hashed password)</li>
 *     <li>User login (validate credentials via Spring Security and generate JWT token)</li>
 * </ul>
 *
 * <p><b>Beginner note:</b> We let Spring Security validate the username + password
 * (using {@link AuthenticationManager}). That means we do not manually compare passwords
 * in the service when logging in.</p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRespository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JWTService jwtService;

    /**
     * Creates the service with all required dependencies.
     *
     * @param userRespository       repository for user database operations
     * @param passwordEncoder       encoder used to hash user passwords (e.g., BCrypt)
     * @param userMapper            mapper converting {@link UserEntity} to DTOs
     * @param jwtService            service responsible for JWT creation
     * @param authenticationManager Spring Security authentication entry point for login validation
     */
    public AuthServiceImpl(UserRespository userRespository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           JWTService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRespository = userRespository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user.
     *
     * <p>Steps:</p>
     * <ol>
     *     <li>Check if email/username already exists</li>
     *     <li>Hash the password using {@link PasswordEncoder}</li>
     *     <li>Save the user</li>
     *     <li>Return DTO response</li>
     * </ol>
     *
     * <p><b>Beginner note:</b> Passwords must never be stored in plain text.
     * We store only the hashed version.</p>
     *
     * @param user incoming registration request
     * @return created user as {@link UserResponse}
     * @throws DuplicateEmailException if the email/username is already registered
     */
    @Transactional
    @Override
    public UserResponse createUser(UserRegistryRequest user) {

        // Normalize username/email to avoid duplicates caused by trailing spaces.
        String username = user.username().trim();

        // Check uniqueness before we insert a new row into the database.
        if (userRespository.existsByUsername(username)) {
            throw new DuplicateEmailException("Email je již registrován");
        }

        // Hash the password (BCrypt, etc.) so we never store the raw password.
        String hashedPassword = passwordEncoder.encode(user.password());

        // Create the user entity (basic constructor-based approach).
        UserEntity userEntity = new UserEntity(
                user.name(),
                user.username().trim(),
                hashedPassword
        );
        // Persist the user and convert the result to DTO for API response.
        UserEntity saved = userRespository.save(userEntity);
        return userMapper.toDTO(saved);
    }

    /**
     * Authenticates a user and returns an access token (JWT).
     *
     * <p>This method delegates credential validation to Spring Security:</p>
     * <ul>
     *     <li>{@link AuthenticationManager} authenticates the username + password</li>
     *     <li>Internally Spring uses {@code DaoAuthenticationProvider} +
     *         {@code UserDetailsService} + {@code PasswordEncoder}</li>
     * </ul>
     *
     * <p><b>Beginner note:</b> If credentials are wrong, Spring throws
     * {@link BadCredentialsException} automatically.</p>
     *
     * @param loginRequest incoming login request
     * @return login response containing user info + JWT access token
     * @throws BadCredentialsException when credentials are invalid
     * @throws NotFoundException       when the authenticated user cannot be found in DB (should be rare)
     */
    @Override
    public LoginResponse loadUser(LoginRequest loginRequest) {

        // Normalize username/email for consistent authentication behavior.
        String username = loginRequest.username().trim();

        // Ask Spring Security to validate username + password.
        // If invalid, authenticate() throws BadCredentialsException.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        loginRequest.password()
                )
        );

        // Spring returns the authenticated principal (usually UserDetails).
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        // We still load the user entity to get extra data like ID and display name.
        // (UserDetails often contains only username + roles/authorities.)
        UserEntity user = userRespository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Email nenalezen: " + principal.getUsername()));

        // Generate access token (JWT) for the authenticated principal.
        String accessToken = jwtService.generateToken(user);

        // Return the login response expected by the frontend.
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                accessToken
        );
    }
}

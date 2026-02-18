package cz.timetracker.service;

import cz.timetracker.configuration.JWTService;
import cz.timetracker.dto.mapper.UserMapper;
import cz.timetracker.dto.user.LoginRequest;
import cz.timetracker.dto.user.LoginResponse;
import cz.timetracker.dto.user.UserRegistryRequest;
import cz.timetracker.dto.user.UserResponse;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.service.exceptions.DuplicateEmailException;
import cz.timetracker.service.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private AutoCloseable mocks;

    @Mock
    private UserRespository userRespository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JWTService jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRespository, passwordEncoder, userMapper, jwtService, authentication -> authentication);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createUser_createsMappedUserWhenUsernameNotExists() {
        UserRegistryRequest request = new UserRegistryRequest("john@example.com", "John", "secret");

        UserEntity saved = new UserEntity("John", "john@example.com", "encoded");
        saved.setId(1L);
        saved.setCreatedAt(LocalDateTime.now());

        UserResponse expected = new UserResponse(1L, "john@example.com", "John", saved.getCreatedAt());

        when(userRespository.existsByUsername("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRespository.save(org.mockito.ArgumentMatchers.any(UserEntity.class))).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(expected);

        UserResponse result = authService.createUser(request);

        assertThat(result).isEqualTo(expected);
        verify(passwordEncoder).encode("secret");
    }

    @Test
    void createUser_throwsDuplicateWhenUsernameAlreadyExists() {
        UserRegistryRequest request = new UserRegistryRequest("john@example.com", "John", "secret");
        when(userRespository.existsByUsername("john@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> authService.createUser(request));

        verify(userRespository, never()).save(org.mockito.ArgumentMatchers.any(UserEntity.class));
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void createUser_trimsUsernameBeforeSave() {
        UserRegistryRequest request = new UserRegistryRequest("  john@example.com  ", "John", "secret");

        UserEntity saved = new UserEntity("John", "john@example.com", "encoded");
        UserResponse dto = new UserResponse(1L, "john@example.com", "John", LocalDateTime.now());

        when(userRespository.existsByUsername("  john@example.com  ")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRespository.save(org.mockito.ArgumentMatchers.any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDTO(org.mockito.ArgumentMatchers.any(UserEntity.class))).thenReturn(dto);

        authService.createUser(request);

        org.mockito.ArgumentCaptor<UserEntity> captor = org.mockito.ArgumentCaptor.forClass(UserEntity.class);
        verify(userRespository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("john@example.com");
    }

    @Test
    void loadUser_returnsTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("john@example.com", "secret");

        UserEntity user = new UserEntity("John", "john@example.com", "encoded");
        user.setId(7L);

        when(userRespository.findByUsername("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse result = authService.loadUser(request);

        assertThat(result.id()).isEqualTo(7L);
        assertThat(result.username()).isEqualTo("john@example.com");
        assertThat(result.name()).isEqualTo("John");
        assertThat(result.accessToken()).isEqualTo("jwt-token");
    }

    @Test
    void loadUser_throwsNotFoundWhenUsernameMissing() {
        LoginRequest request = new LoginRequest("missing@example.com", "secret");
        when(userRespository.findByUsername("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.loadUser(request));

        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void loadUser_throwsBadCredentialsWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("john@example.com", "wrong");

        UserEntity user = new UserEntity("John", "john@example.com", "encoded");
        when(userRespository.findByUsername("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.loadUser(request));

        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.any());
    }
}
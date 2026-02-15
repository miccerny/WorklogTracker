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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImpl implements  AuthService{

    private final UserRespository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JWTService jwtService;

    public AuthServiceImpl(UserRespository userRespository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           JWTService jwtService){
        this.userRespository = userRespository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    @Transactional
    @Override
    public UserResponse createUser(UserRegistryRequest user) {
        if(userRespository.existsByUsername(user.username())){
            throw new DuplicateEmailException("Email je již registrován");
        }

        UserEntity userEntity = new UserEntity(
                user.name(),
                user.username(),
                passwordEncoder.encode(user.password())
        );

        UserEntity saved = userRespository.save(userEntity);
        return userMapper.toDTO(saved);
    }

    @Override
    public LoginResponse loadUser(LoginRequest loginRequest) {

        UserEntity user = userRespository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new NotFoundException("Email nenalezen"));

        if(!passwordEncoder.matches(loginRequest.password(),
        user.getPassword())){
            throw new BadCredentialsException("Neplatné přihlašovací údaje");
        }

        String accessToken = jwtService.generateToken(user);
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                accessToken
        );
    }
}

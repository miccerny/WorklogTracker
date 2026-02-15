package cz.timetracker.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImpl implements  AuthService{

    private final UserRespository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRespository userRespository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper){
        this.userRespository = userRespository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserResponse createUser(UserRegistryRequest user) {
        if(userRespository.existByUsername(user.username())){
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

        UserEntity user = userRespository.findByEmail(loginRequest.username())
                .orElseThrow(() -> new NotFoundException("Email nenalezen"));



    }
}

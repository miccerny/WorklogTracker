package cz.timetracker.service;

<<<<<<< Updated upstream
public class AuthService {
    
=======
import cz.timetracker.dto.user.LoginRequest;
import cz.timetracker.dto.user.LoginResponse;
import cz.timetracker.dto.user.UserRegistryRequest;
import cz.timetracker.dto.user.UserResponse;

public interface AuthService {

    UserResponse createUser(UserRegistryRequest user);

    LoginResponse loadUser(LoginRequest loginRequest);

    UserResponse getCurrentUser();
>>>>>>> Stashed changes
}

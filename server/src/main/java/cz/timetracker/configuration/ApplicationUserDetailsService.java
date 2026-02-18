package cz.timetracker.configuration;

import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.service.exceptions.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Spring Security {@link UserDetailsService} implementation backed by the database.
 *
 * <p>Spring Security calls this service whenever it needs to load a user by username
 * (in this project the username is the user's email).</p>
 *
 * <p><b>Beginner note:</b> This class does not authenticate the user by itself. It only
 * loads the user record. Authentication happens elsewhere (e.g., during login or when
 * validating a JWT token).</p>
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRespository userRespository;

    /**
     * Creates the service with required dependencies.
     *
     * @param userRespository repository used to load users from the database
     */
    public ApplicationUserDetailsService(UserRespository userRespository){
        this.userRespository = userRespository;
    }

    /**
     * Loads a user from the database by username (email).
     *
     * <p>If the user does not exist, we throw {@link NotFoundException}. Spring Security
     * will treat this as "user not found" during authentication.</p>
     *
     * @param username username/email coming from Spring Security
     * @return loaded {@link UserDetails} instance
     * @throws NotFoundException when the user cannot be found
     */
    @Override
    public UserDetails loadUserByUsername(String username){
        return userRespository.findByUsername(username).
                orElseThrow(() -> new NotFoundException("Uživatel nebyl nalezen"));

    }
}

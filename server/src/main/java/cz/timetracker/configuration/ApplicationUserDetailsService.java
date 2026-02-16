package cz.timetracker.configuration;

import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.service.exceptions.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Jednoduchá implementace UserDetailsService.
 *
 * <p>Spring Security ji používá vždy, když potřebuje načíst uživatele podle username.
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRespository userRespository;

    public ApplicationUserDetailsService(UserRespository userRespository){
        this.userRespository = userRespository;
    }

    /**
     * Načte uživatele z databáze podle username (emailu).
     *
     * @param username email/username uživatele
     * @return UserDetails objekt
     */
    @Override
    public UserDetails loadUserByUsername(String username){
        return userRespository.findByUsername(username).
                orElseThrow(() -> new NotFoundException("Uživatel nebyl nalezen"));

    }
}

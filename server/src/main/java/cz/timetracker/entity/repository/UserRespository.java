package cz.timetracker.entity.repository;

import cz.timetracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for {@link UserEntity}.
 *
 * <p>This repository provides database operations related to users,
 * including lookup methods used during registration and authentication.</p>
 *
 * <p>By extending {@link JpaRepository}, this interface automatically
 * inherits common CRUD operations such as:</p>
 * <ul>
 *     <li>save()</li>
 *     <li>findById()</li>
 *     <li>findAll()</li>
 *     <li>delete()</li>
 * </ul>
 *
 * <p><b>Note</b>
 * Spring Data JPA automatically generates implementations
 * based on method names. No manual SQL or implementation class is required.</p>
 */
public interface UserRespository extends JpaRepository<UserEntity, Long> {

    /**
     * Checks whether a user with the given username already exists.
     *
     * <p>Commonly used during registration to prevent duplicate accounts.</p>
     *
     * @param username unique login identifier (typically email)
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Finds a user by username.
     *
     * <p>This method is typically used during authentication
     * to load user details from the database.</p>
     *
     * @param username unique login identifier
     * @return optional containing {@link UserEntity} if found
     */
    Optional<UserEntity> findByUsername(String username);
}

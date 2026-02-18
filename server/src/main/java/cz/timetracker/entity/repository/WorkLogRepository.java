package cz.timetracker.entity.repository;

import cz.timetracker.entity.WorkLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link WorkLogEntity}.
 *
 * <p>This repository provides database access for WorkLogs,
 * including ownership-based queries used for authorization checks.</p>
 *
 * <p>By extending {@link JpaRepository}, this interface inherits
 * standard CRUD operations such as:</p>
 * <ul>
 *     <li>save()</li>
 *     <li>findById()</li>
 *     <li>findAll()</li>
 *     <li>delete()</li>
 * </ul>
 *
 * <p><b>Beginner note:</b>
 * Spring Data JPA generates the implementation automatically
 * based on method naming conventions.</p>
 */
public interface WorkLogRepository extends JpaRepository<WorkLogEntity, Long> {

    /**
     * Returns all WorkLogs belonging to a specific user.
     *
     * <p>Commonly used to display a user's projects/dashboard.</p>
     *
     * @param userId ID of the owner (User)
     * @return list of {@link WorkLogEntity} owned by the user
     */
    List<WorkLogEntity> findByOwnerId(Long userId);

    /**
     * Finds a WorkLog by its ID and verifies ownership.
     *
     * <p>This method is typically used to ensure that
     * a user can only access their own WorkLogs.</p>
     *
     * @param id WorkLog ID
     * @param ownerId ID of the owner
     * @return optional containing the WorkLog if found and owned by the user
     */
    Optional<WorkLogEntity> findByIdAndOwnerId(Long id, Long ownerId);

    /**
     * Checks whether a WorkLog exists and belongs to a specific user.
     *
     * <p>Useful for lightweight authorization validation
     * before performing update or delete operations.</p>
     *
     * <p><b>Beginner note:</b>
     * This avoids loading the full entity from the database
     * when we only need to verify existence.</p>
     *
     * @param id WorkLog ID
     * @param ownerId ID of the owner
     * @return true if WorkLog exists and belongs to the user
     */
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}

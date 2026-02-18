package cz.timetracker.entity.repository;

import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.enums.TimerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link TimerEntity}.
 *
 * <p>This repository provides database operations related to timers,
 * including custom queries for active timers, ownership validation,
 * and ordered history retrieval.</p>
 *
 * <p><b>Note:</b>
 * Spring Data JPA automatically generates implementations
 * for methods based on naming conventions or {@code @Query} annotations.</p>
 */
public interface TimerRepository extends JpaRepository<TimerEntity, Long> {

    /**
     * Checks whether a timer with a specific status exists
     * for the given WorkLog.
     *
     * <p>This method uses a native SQL query to check existence efficiently.</p>
     *
     * <p><b>Beginner note:</b>
     * {@code select exists (...)} returns true/false directly
     * without loading full entity data from the database.</p>
     *
     * @param id WorkLog ID
     * @param timerType status of timer (e.g. RUNNING, STOPPED)
     * @return true if such timer exists
     */
    @Query(value = """
                select exists (
                    select 1
                    from timer_entity t
                    where t.work_log_id = :workLogId
                      and t.status = :status
                )
            """, nativeQuery = true)
    boolean existsByWorkLogIdAndStatus(Long id, TimerType timerType);

    /**
     * Checks whether a timer exists for a given WorkLog
     * and specific owner.
     *
     * <p>This is typically used for authorization validation
     * before allowing certain operations.</p>
     *
     * @param worklogId WorkLog ID
     * @param user owner of the timer
     * @return true if such timer exists
     */
    boolean existsByWorkLogIdAndOwner(Long worklogId, UserEntity user);

    /**
     * Returns the most recent timer with a given status
     * for a specific WorkLog and owner.
     *
     * <p>This method uses JPQL (not native SQL).</p>
     *
     * <p>It:
     * <ul>
     *     <li>filters by WorkLog ID</li>
     *     <li>filters by timer status</li>
     *     <li>filters by owner</li>
     *     <li>orders by startedAt descending</li>
     *     <li>returns the newest timer</li>
     * </ul>
     * </p>
     *
     * <p><b>Beginner note:</b>
     * JPQL works with entity names and fields (not table/column names).</p>
     *
     * @param id WorkLog ID
     * @param timerType status of timer
     * @param user owner of the timer
     * @return optional containing latest matching timer
     */
    @Query("""
                select t
                from TimerEntity t
                where t.work_log.id = :workLogId
                  and t.status = :status
                  and t.owner = :owner
                order by t.startedAt desc
            """)
    Optional<TimerEntity> findLatestForWorkLog(Long id, TimerType timerType, UserEntity user);

    /**
     * Finds a timer by its ID and verifies ownership
     * through WorkLog owner.
     *
     * <p>Useful for secure access control validation.</p>
     *
     * @param id timer ID
     * @param ownerId owner (user) ID
     * @return optional containing timer if found and owned by user
     */
    Optional<TimerEntity> findByIdAndWorkLogOwnerId(Long id, Long ownerId);

    /**
     * Returns all timers for a specific WorkLog,
     * ordered from newest to oldest.
     *
     * <p>Commonly used to display timer history in the UI.</p>
     *
     * <p><b>Beginner note:</b>
     * Spring builds the query automatically based on the method name.</p>
     *
     * @param id WorkLog ID
     * @return list of timers sorted by startedAt descending
     */
    List<TimerEntity> findByWorkLogIdOrderByStartedAtDesc(Long id);
}

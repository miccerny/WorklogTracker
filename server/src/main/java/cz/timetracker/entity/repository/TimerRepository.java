package cz.timetracker.entity.repository;


import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.enums.TimerType;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
=======
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
>>>>>>> Stashed changes

import java.util.Optional;

public interface TimerRepository extends JpaRepository<TimerEntity, Long> {

    boolean existsByWorkLogAndStatus(Long id, TimerType timerType);

<<<<<<< Updated upstream

    Optional<TimerEntity> findFirstByWorkLogIdAndStatusOrderByStartedAtDesc(Long id, TimerType timerType);
=======
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
    boolean existsByWorkLogIdAndWorkLogOwner(Long worklogId, UserEntity user);

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
                where t.workLog.id = :workLogId
                  and t.status = :status
                  and t.workLog.owner = :owner
                order by t.startedAt desc
            """)
    Optional<TimerEntity> findLatestForWorkLog(@Param("workLogId") Long id,
                                                @Param("status") TimerType timerType,
                                                @Param("owner") UserEntity user);

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
>>>>>>> Stashed changes
}

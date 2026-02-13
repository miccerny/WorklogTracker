package cz.timetracker.entity.repository;

import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.enums.TimerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TimerEntity.
 *
 * This interface extends JpaRepository which automatically provides
 * basic CRUD operations (save, delete, findById, etc.).
 *
 * Additional query methods are defined using Spring Data JPA
 * method name conventions. Spring generates their implementation automatically.
 */
public interface TimerRepository extends JpaRepository<TimerEntity, Long> {

    /**
     * Checks whether a timer with a specific status exists
     * for given WorkLog ID.
     *
     * Used typically to verify if there is already a running timer.
     *
     * Spring generates query based on method name:
     * WHERE work_log_id = ? AND status = ?
     *
     * @param id WorkLog ID
     * @param timerType status of timer (e.g. RUNNING, STOPPED)
     * @return true if such timer exists
     */
    boolean existsByWorkLogIdAndStatus(Long id, TimerType timerType);

    /**
     * Checks whether any timer exists for given WorkLog.
     *
     * Useful for validation before deleting WorkLog, etc.
     *
     * @param id WorkLog ID
     * @return true if at least one timer exists
     */
    boolean existsByWorkLogId(Long id);

    /**
     * Returns the most recent timer with given status
     * for specific WorkLog.
     *
     * "findFirstBy" + "OrderByStartedAtDesc"
     * means Spring will:
     * - filter by workLogId and status
     * - sort by startedAt descending
     * - return the first record
     *
     * This is commonly used to get currently running timer.
     *
     * @param id WorkLog ID
     * @param timerType status of timer
     * @return Optional containing timer if found
     */
    Optional<TimerEntity> findFirstByWorkLogIdAndStatusOrderByStartedAtDesc(Long id, TimerType timerType);


    /**
     * Returns all timers for given WorkLog,
     * ordered from newest to oldest.
     *
     * Useful for displaying timer history in UI.
     *
     * @param id WorkLog ID
     * @return list of timers sorted by startedAt descending
     */
    List<TimerEntity> findByWorkLogIdOrderByStartedAtDesc(Long id);
}

package cz.timetracker.entity;

import cz.timetracker.entity.enums.TimerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity representing a single Timer.
 *
 * <p>A Timer belongs to one WorkLog and represents a measurable
 * time interval between start and stop.</p>
 *
 * <p>It stores:</p>
 * <ul>
 *     <li>Start timestamp</li>
 *     <li>Stop timestamp (nullable if still running)</li>
 *     <li>Pre-calculated duration in seconds</li>
 *     <li>Status (e.g. RUNNING, STOPPED)</li>
 *     <li>Optional note</li>
 * </ul>
 *
 * <p><b>Beginner note:</b>
 * This class is a JPA entity mapped to a database table.
 * It should not contain business logic such as duration calculations.
 * That logic belongs in the service layer.</p>
 */
@Entity
@Getter
@Setter
public class TimerEntity {

    /**
     * Primary key of the timer.
     *
     * <p>The value is generated using a database sequence.</p>
     *
     * <p><b>Beginner note:</b>
     * {@link SequenceGenerator} defines how IDs are generated.
     * {@link GeneratedValue} tells JPA to use that strategy.</p>
     */
    @Id
    @SequenceGenerator(sequenceName = "timer_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Timestamp when the timer was started.
     *
     * <p>This is set when the user starts the timer.</p>
     */
    @Column(nullable = false)
    private LocalDateTime startedAt;

    /**
     * Timestamp when the timer was stopped.
     *
     * <p>May be null if the timer is currently running.</p>
     */
    @Column
    private LocalDateTime stoppedAt;

    /**
     * Total duration of the timer in seconds.
     *
     * <p>This value is typically calculated when the timer is stopped
     * and then persisted to improve performance.</p>
     *
     * <p><b>Note:</b>
     * Storing the calculated duration avoids recalculating it
     * every time the timer is displayed.</p>
     */
    @Column
    private Long durationInSeconds;

    /**
     * Current status of the timer.
     *
     * <p>Stored as a String in the database
     * (e.g. "RUNNING", "STOPPED").</p>
     *
     * <p><b>Beginner note:</b>
     * {@link EnumType#STRING} is safer than ORDINAL because
     * changing enum order will not break existing data.</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimerType status;

    /**
     * Optional note associated with the timer.
     *
     * <p>Can be used to describe what activity was performed.</p>
     */
    @Column
    private String note;

    /**
     * Many-to-One relationship:
     * Many timers can belong to one WorkLog.
     *
     * <p>This creates a foreign key column (work_log_id)
     * in the timer table.</p>
     *
     * <p><b>Note:</b>
     * The {@code optional = false} means that every timer
     * must be associated with a WorkLog.</p>
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "work_log_id")
    private WorkLogEntity workLog;

}

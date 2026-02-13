package cz.timetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA entity representing a WorkLog (project or activity group).
 *
 * <p>Each WorkLog belongs to exactly one User
 * and can contain multiple Timers.
 *
 * <p>This entity stores basic metadata about the project,
 * while individual time tracking is handled by TimerEntity.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class WorkLogEntity {

    /**
     * Primary key of the WorkLog.
     */
    @Id
    @SequenceGenerator(sequenceName = "project_name_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Name of the WorkLog (e.g. "Backend development").
     */
    @Column(nullable = false)
    private String workLogName;


    @Column
    private Float hourlyRate;

    @Column(nullable = false)
    private boolean activated;

    /**
     * Timestamp when WorkLog was created.
     */
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * One-to-Many relationship:
     * One WorkLog can contain multiple Timers.
     *
     * mappedBy = "workLog" → owning side is in TimerEntity.
     *
     * CascadeType.ALL ensures timers are persisted/removed
     * together with the WorkLog.
     */
    @OneToMany(mappedBy = "workLog", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TimerEntity> timers;

}

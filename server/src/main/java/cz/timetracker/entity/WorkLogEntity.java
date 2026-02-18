package cz.timetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
     *
     * <p>Generated using a database sequence.</p>
     */
    @Id
    @SequenceGenerator(sequenceName = "project_name_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Name of the WorkLog (e.g., "Backend development").
     *
     * <p>This is the main identifier visible to the user.</p>
     */
    @Column(nullable = false)
    private String workLogName;

    /**
     * Hourly rate associated with this WorkLog.
     *
     * <p>Can be used to calculate cost based on tracked duration.</p>
     */
    @Column
    private Float hourlyRate;

    /**
     * Indicates whether the WorkLog is active.
     *
     * <p>Deactivating a WorkLog may prevent new timers
     * from being created.</p>
     */
    @Column(nullable = false)
    private boolean activated;

    /**
     * Timestamp when the WorkLog was created.
     *
     * <p><b>Note:</b>
     * {@link CreationTimestamp} automatically sets this value
     * when the entity is first persisted.</p>
     */
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * One-to-Many relationship:
     * One WorkLog can contain multiple Timers.
     *
     * <p><b>Mapping details:</b></p>
     * <ul>
     *     <li>{@code mappedBy = "workLog"} → the owning side is in {@link TimerEntity}</li>
     *     <li>{@code cascade = CascadeType.ALL} → timers are persisted/removed with WorkLog</li>
     *     <li>{@code orphanRemoval = true} → removing a timer from the list deletes it</li>
     * </ul>
     */
    @OneToMany(mappedBy = "workLog", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TimerEntity> timers;

    /**
     * Many-to-One relationship:
     * Each WorkLog belongs to one User (owner).
     *
     * <p>This creates a foreign key column {@code owner_id}
     * in the WorkLog table.</p>
     *
     * <p><b>Note:</b>
     * The owning side of the relationship is here
     * because this entity contains the {@code @JoinColumn}.</p>
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

}

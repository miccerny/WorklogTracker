package cz.timetracker.entity;

import cz.timetracker.entity.enums.TimerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class TimerEntity {

    @Id
    @SequenceGenerator(sequenceName = "timer_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Instant startedAt;

    @Column
    private Instant stoppedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimerType status;

    @Column
    private String note;

    @ManyToOne(optional = false)
    @JoinColumn(name = "work_log_id")
    private WorkLogEntity workLog;

}

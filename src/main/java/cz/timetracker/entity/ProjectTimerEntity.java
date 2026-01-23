package cz.timetracker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTimerEntity {

    @Id
    @SequenceGenerator
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String projectName;

    @Column
    private Float hourlyRate;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

package cz.timetracker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class WorkLogEntity {

    @Id
    @SequenceGenerator(sequenceName = "project_name_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("_id")
    private Long id;

    @Column(nullable = false)
    private String workLogName;

    @Column
    private Float hourlyRate;

    @Column(nullable = false)
    private boolean activated;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}

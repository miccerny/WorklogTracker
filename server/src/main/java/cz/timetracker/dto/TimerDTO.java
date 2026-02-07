package cz.timetracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.timetracker.entity.enums.TimerType;
import jdk.jfr.Timestamp;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class TimerDTO {

    private Long id;

    private LocalDateTime createdAt;

    private String note;

    private Long workLogId;

    private Long durationInSeconds;

    private TimerType status;
}

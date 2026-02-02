package cz.timetracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class TimerDTO {

    @JsonProperty("_id")
    private Long id;

    private LocalDateTime createdAt;

    private String note;
}

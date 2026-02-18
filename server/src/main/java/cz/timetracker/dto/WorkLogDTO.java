package cz.timetracker.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class WorkLogDTO {

    @Id
    private Long id;

    @NotBlank(message = "Jméno projektu nemsí být prázdné")
    private String workLogName;

    private Float hourlyRate;

    private List<TimerDTO> timerDTOs;

}

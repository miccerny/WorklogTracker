package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.entity.enums.TimerType;

import java.util.List;

public interface TimerService {

    TimerDTO startTimer(Long id);

    TimerDTO stopTimer(Long id);

    List<TimerDTO> getAllTimers(Long workLogId);
}

package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;

public interface TimerService {

    TimerDTO startTimer(Long id);

    TimerDTO stopTimer(Long id);
}

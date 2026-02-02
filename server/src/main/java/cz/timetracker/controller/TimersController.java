package cz.timetracker.controller;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.entity.enums.TimerType;
import cz.timetracker.service.TimerService;
import cz.timetracker.service.WorkLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worklogs")
public class TimersController{

    private final TimerService timerService;
    private final WorkLogService workLogService;

    public TimersController (TimerService timerService,
                             WorkLogService workLogService){
        this.timerService = timerService;
        this.workLogService = workLogService;
    }

    @GetMapping("/{workLogId}/timers")
    public List<TimerDTO> getProject(@PathVariable Long workLogId){
        return timerService.getAllTimers(workLogId);
    }

    @PostMapping("/{workLogId}/startTimer")
    public TimerDTO startTimer(@PathVariable Long workLogId){
        return timerService.startTimer(workLogId);
    }

    @PostMapping("/{workLogId}/stopTimer")
    public TimerDTO stopTimer( @PathVariable Long workLogId){
        return timerService.stopTimer(workLogId);
    }
}

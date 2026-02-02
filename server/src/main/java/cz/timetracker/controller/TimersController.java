package cz.timetracker.controller;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.service.TimerService;
import cz.timetracker.service.WorkLogService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/timers")
    public WorkLogDTO getProject(@RequestParam Long projectId){
        return workLogService.getProject(projectId);
    }

    @PostMapping("/{workLogId}/startTimer")
    public TimerDTO startTimer(Long workLogId){
        return timerService.startTimer(workLogId);
    }

    @PostMapping("/{workLogId}/stopTimer")
    public TimerDTO stopTimer(Long workLogId){
        return timerService.stopTimer(workLogId);
    }
}

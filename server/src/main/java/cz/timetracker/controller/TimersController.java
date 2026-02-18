package cz.timetracker.controller;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.service.TimerService;
import cz.timetracker.service.WorkLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for handling HTTP requests related to Timers.
 *
 * <p>This controller acts as an entry point between the frontend (React app)
 * and the backend business logic (TimerService).
 *
 * <p>Main responsibility:
 * <ul>
 *     <li>Receive HTTP requests</li>
 *     <li>Delegate logic to the service layer</li>
 *     <li>Return properly formatted HTTP responses</li>
 * </ul>
 *
 * <p>Note: This class does NOT contain business logic.
 * All calculations and rules should be implemented inside the service layer.
 */
@RestController
@RequestMapping("/api/worklogs")
public class TimersController{

    private final TimerService timerService;

    /**
     * Constructor-based dependency injection.
     *
     * <p>Spring automatically injects TimerService bean here.
     * This is the recommended way because:
     * <ul>
     *     <li>Field is immutable (final)</li>
     *     <li>Better for testing</li>
     *     <li>Clear dependency declaration</li>
     * </ul>
     *
     * @param timerService service layer responsible for timer logic
     */
    public TimersController (TimerService timerService,
                             WorkLogService workLogService){
        this.timerService = timerService;
    }

    @GetMapping("/{workLogId}/summary")
    public List<TimerDTO> getProject(@PathVariable Long workLogId){
        return timerService.getAllTimers(workLogId);
    }

    /**
     * Starts a new timer under a specific WorkLog.
     *
     * <p>Flow:
     * <ul>
     *     <li>Frontend sends POST request</li>
     *     <li>Controller delegates to service</li>
     *     <li>Service creates and stores new timer (with startedAt timestamp)</li>
     *     <li>Response DTO is returned to frontend</li>
     * </ul>
     *
     * @param workLogId ID of the WorkLog under which the timer should be created
     * @return ResponseEntity containing created TimerResponseDto
     */
    @PostMapping("/{workLogId}/startTimer")
    public TimerDTO startTimer(@PathVariable Long workLogId){
        // Call service layer to create, start a new timer and HTTP 200 OK with response body
        return timerService.startTimer(workLogId);
    }

    /**
     * Stops an existing timer.
     *
     * <p>Flow:
     * <ul>
     *     <li>Frontend sends POST request to stop specific timer</li>
     *     <li>Controller calls service layer</li>
     *     <li>Service calculates duration (difference between start and stop)</li>
     *     <li>Timer entity is updated in database</li>
     * </ul>
     *
     * @param workLogId ID of the parent WorkLog
     * @return ResponseEntity containing updated TimerResponseDto
     */
    @PostMapping("/{workLogId}/stopTimer")
    public TimerDTO stopTimer( @PathVariable Long workLogId){
        return timerService.stopTimer(workLogId);
    }

    /**
     * Returns the active timer for the given WorkLog.
     *
     * <p>In this project, an "active timer" typically means a timer that is currently running
     * (for example: it has a start time and does not have an end time yet).</p>
     *
     * <p><b>Note</b> Any validation (worklog existence, ownership, active timer existence)
     * should happen inside the service layer. The controller only delegates the request.</p>
     *
     * @param workLogId ID of the WorkLog for which we want to find the active timer
     * @return {@link TimerDTO} representing the active timer
     */
    @GetMapping("/{workLogId}/active-timer")
    public TimerDTO getActiveTimer(@PathVariable Long workLogId){
        // Delegate business logic to the service layer.
        // (Controller should stay thin: input -> service -> output.)
        return  timerService.getActiveTimer(workLogId);
    }

    /**
     * Stops an active timer.
     *
     * <p>This endpoint is designed to change the timer state from "running" to "stopped".
     * The actual stop logic (setting end time, computing duration, validations, persistence)
     * is handled by the service layer.</p>
     *
     * <p><b>Note:</b> We use PUT here because we are updating an existing timer resource
     * (we are not creating a new record).</p>
     *
     * @param timerId ID of the timer that should be stopped
     * @return {@link TimerDTO} representing the updated timer after it was stopped
     */
    @PutMapping("/active-timer/{timerId}")
    public TimerDTO stopActiveTimer(@PathVariable Long timerId){
        // Delegate the update operation to the service layer.
        return timerService.stopActiveTimer(timerId);
    }
}

package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;

import java.util.List;

/**
 * Service interface defining business operations
 * related to Timer logic.
 *
 * This layer separates controller (HTTP layer)
 * from actual business logic implementation.
 *
 * Implementation is provided in TimerServiceImpl.
 */
public interface TimerService {

    /**
     * Starts a new timer for given WorkLog.
     *
     * Expected behavior:
     * - Validate whether another timer is already running
     * - Create new Timer entity
     * - Persist it
     *
     * @param id
     * @return created Timer as response DTO
     */
    TimerDTO startTimer(Long id);

    /**
     * Stops currently running timer for given WorkLog.
     *
     * Expected behavior:
     * - Find active timer
     * - Set stopped timestamp
     * - Calculate duration
     * - Persist changes
     *
     * @param id ID of timer
     * @return updated Timer as response DTO
     */
    TimerDTO stopTimer(Long id);


    /**
     * Returns list of timers for given WorkLog.
     *
     * Typically used to display timer history.
     *
     * @param workLogId ID of WorkLog
     * @return list of TimerResponseDto
     */
    List<TimerDTO> getAllTimers(Long workLogId);
}

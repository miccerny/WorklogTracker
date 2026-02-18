package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.dto.mapper.TimerMapper;
import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.enums.TimerType;
import cz.timetracker.entity.repository.TimerRepository;
import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.entity.repository.WorkLogRepository;
import cz.timetracker.service.exceptions.ConflictException;
import cz.timetracker.service.exceptions.ForbiddenException;
import cz.timetracker.service.exceptions.NotFoundException;
import cz.timetracker.service.exceptions.TimerAlreadyRunningException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Timer operations.
 *
 * <p>Responsibilities of this service:
 * <ul>
 *   <li>Start timer for a given WorkLog</li>
 *   <li>Stop currently running timer for a given WorkLog</li>
 *   <li>Return list of timers for a given WorkLog</li>
 * </ul>
 *
 * <p>Business rule handled here:
 * If a timer crosses midnight (starts one day and ends next day),
 * it is split into two records so each timer belongs to one calendar day.
 */
@Service
public class TimerServiceImpl implements TimerService{

    private final TimerRepository timerRepository;
    private final TimerMapper timerMapper;
    private final WorkLogRepository workLogRepository;
    private final UserRespository userRespository;

    /**
     * Constructor injection of required dependencies.
     *
     * <p>Spring creates this service as a bean and injects repositories + mapper automatically.
     *
     * @param timerRepository repository for TimerEntity (database operations)
     * @param timerMapper mapper to convert entity <-> DTO
     * @param workLogRepository repository for WorkLogEntity
     */
    public TimerServiceImpl(TimerRepository timerRepository,
                            TimerMapper timerMapper,
                            WorkLogRepository workLogRepository,
                            UserRespository userRespository){
        this.timerRepository = timerRepository;
        this.timerMapper = timerMapper;
        this.workLogRepository = workLogRepository;
        this.userRespository = userRespository;
    }

    /**
     * Starts a new timer for the given WorkLog.
     *
     * <p>Main flow:
     * <ol>
     *   <li>Check if there is already a RUNNING timer for this WorkLog</li>
     *   <li>Load WorkLog from DB (must exist)</li>
     *   <li>Create new TimerEntity with startedAt = now and status RUNNING</li>
     *   <li>Save to DB</li>
     *   <li>Return mapped DTO</li>
     * </ol>
     *
     * @param workLogId ID of the WorkLog where timer should start
     * @return started timer as DTO
     * @throws ConflictException when a timer is already running for this WorkLog
     * @throws NotFoundException when WorkLog does not exist
     */
    @Transactional
    @Override
    public TimerDTO startTimer(Long workLogId) {
        UserEntity user = getCurrentUser();
        WorkLogEntity workLog = requireOwnedWorkLog(workLogId, user.getId());
        // Business validation: allow only one RUNNING timer per WorkLog.
        if(timerRepository.existsByWorkLogIdAndStatus(workLogId, TimerType.RUNNING)){
            throw new TimerAlreadyRunningException("Timer already running for this worklog");
        }

        // Create new timer: start time = now, status = RUNNING, assign to WorkLog.
        TimerEntity timerEntity = new TimerEntity();
        timerEntity.setStartedAt(LocalDateTime.now());
        timerEntity.setStatus(TimerType.RUNNING);
        timerEntity.setWorkLog(workLog);

        // Persist timer to DB so it gets its ID and is stored permanently.
        TimerEntity saved = timerRepository.save(timerEntity);

        // Map DB entity to DTO so controller/front-end gets only needed fields.
        return timerMapper.toDTO(saved);
    }

    /**
     * Stops the currently running timer for given WorkLog.
     *
     * <p>Main flow:
     * <ol>
     *   <li>Find the most recent RUNNING timer</li>
     *   <li>Load WorkLog (must exist)</li>
     *   <li>Stop timer (set stoppedAt, status STOPPED, durationInSeconds)</li>
     *   <li>If timer crosses midnight, split into two STOPPED timers</li>
     *   <li>Return last saved timer as DTO</li>
     * </ol>
     *
     * @param workLogId ID of the WorkLog
     * @return stopped timer as DTO (if split happens, returns the second part)
     * @throws ConflictException when no RUNNING timer exists for this WorkLog
     * @throws NotFoundException when WorkLog does not exist
     */
    @Transactional
    @Override
    public TimerDTO stopTimer(Long workLogId) {
        UserEntity user = getCurrentUser();
        // Find active timer: we expect only one RUNNING timer, take the newest (startedAt DESC).
        TimerEntity runningTimer = timerRepository
                .findLatestForWorkLog(workLogId, TimerType.RUNNING, user)
                .orElseThrow(()-> new ConflictException("Timer is not running for this worklog " + workLogId));

        // WorkLog must exist (we want consistent relationship and validation).
        WorkLogEntity workLogEntity = runningTimer.getWorkLog();

        // Stop time is "now" (moment when user pressed STOP).
        LocalDateTime stoppedAt = LocalDateTime.now();

        // Stop timer and split into 1 or 2 records depending on whether midnight is crossed.
        List<TimerEntity> savedTimers = stopAndSplitTimerIfNeeded(
                runningTimer, stoppedAt, workLogEntity
        );

        // If split happens, lastSaved is the second timer (after midnight part).
        TimerEntity lastSaved = savedTimers.get(savedTimers.size() - 1);

        // Return DTO representation of what we saved (usually what UI should show as "stopped result").
        return  timerMapper.toDTO(lastSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public TimerDTO getActiveTimer(Long workLogId) {
        UserEntity user = getCurrentUser();
        TimerEntity runningTimer = timerRepository.findLatestForWorkLog(workLogId, TimerType.RUNNING, user)
                .orElseThrow(() -> new NotFoundException("No active timer for worklog " + workLogId));

        return timerMapper.toDTO(runningTimer);
    }

    @Transactional
    @Override
    public TimerDTO stopActiveTimer(Long id) {
        UserEntity user = getCurrentUser();
        TimerEntity runningTimer = timerRepository.findByIdAndWorkLogOwnerId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Timer with ID " + id + " not found"));

        // Jediná povolená “úprava”: stop, jen když běží
        if (runningTimer.getStatus() != TimerType.RUNNING) {
            throw new ConflictException("Timer is not running: " + id);
        }

        LocalDateTime stoppedAt = LocalDateTime.now();

        // worklog už na timeru máš, není nutné ho znovu tahat přes repository
        WorkLogEntity workLogEntity = runningTimer.getWorkLog();

        List<TimerEntity> savedTimers = stopAndSplitTimerIfNeeded(runningTimer, stoppedAt, workLogEntity);

        TimerEntity lastSaved = savedTimers.get(savedTimers.size() - 1);
        return timerMapper.toDTO(lastSaved);
    }

    /**
     * Returns all timers for a given WorkLog.
     *
     * <p>Implementation note:
     * This method is read-only transaction because we do not modify DB state here.
     *
     * @param workLogId WorkLog ID
     * @return list of timers mapped to DTOs, ordered by startedAt DESC
     * @throws NotFoundException when no timers exist for given WorkLog ID (according to current validation)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TimerDTO> getAllTimers(Long workLogId) {
        UserEntity user = getCurrentUser();
        // Validation: if there are no timers for workLogId, we currently treat it as "not found".
        // (This depends on your business rules: some apps would return empty list instead.)
        if(!workLogRepository.existsByIdAndOwnerId(workLogId, user.getId())){
            throw new ForbiddenException("No access to worklog");
        }

        // Load timers ordered newest -> oldest and map each entity to DTO.
        return timerRepository.findByWorkLogIdOrderByStartedAtDesc(workLogId).stream()
                    .map(timerMapper::toDTO)
                    .toList();
    }


    /**
     * Stops a RUNNING timer and optionally splits it into two timers if it crosses midnight.
     *
     * <p>Why splitting?
     * If timer starts on day A and ends on day B, this method creates:
     * <ul>
     *   <li>Timer 1: from startedAt -> midnight (start of next day)</li>
     *   <li>Timer 2: from midnight -> stoppedAt</li>
     * </ul>
     *
     * <p>This helps with daily summaries (each timer belongs to one calendar day).
     *
     * @param runningTimer the timer currently in RUNNING status
     * @param stoppedAt timestamp when timer should stop
     * @param workLogEntity parent WorkLog entity
     * @return list of saved timers (size 1 when no split, size 2 when split)
     */
    @Transactional
    private List<TimerEntity> stopAndSplitTimerIfNeeded(TimerEntity runningTimer,
                                                        LocalDateTime stoppedAt,
                                                        WorkLogEntity workLogEntity){
        // Extract calendar date from start and stop time.
        LocalDate startDate = runningTimer.getStartedAt().toLocalDate();
        LocalDate stopDate = stoppedAt.toLocalDate();

        // If start and stop are on the same date, we do simple stop (no split).
        if(startDate.equals(stopDate)){
            runningTimer.setStoppedAt(stoppedAt);
            runningTimer.setStatus(TimerType.STOPPED);

            // Duration is stored in seconds for easier calculation / sorting later.
            runningTimer.setDurationInSeconds(Duration.between(runningTimer.getStartedAt(),
                    stoppedAt).getSeconds());

            // Ensure relationship is set (timer belongs to workLog).
            runningTimer.setWorkLog(workLogEntity);

            // Save and return as single-item list.
            return  List.of(timerRepository.save(runningTimer));
        }

        // If timer crosses midnight, calculate the "midnight boundary":
        // next day at 00:00:00 based on startDate
        LocalDateTime afterMidnight = startDate
                .plusDays(1).atStartOfDay();

        // First part: startedAt -> afterMidnight
        runningTimer.setStoppedAt(afterMidnight);
        runningTimer.setStatus(TimerType.STOPPED);
        runningTimer.setDurationInSeconds(Duration.between(runningTimer.getStartedAt(),
                afterMidnight).getSeconds());
        runningTimer.setWorkLog(workLogEntity);

        // Save first part into DB.
        TimerEntity firstSaved = timerRepository.save(runningTimer);

        TimerEntity overFlowTimer = new TimerEntity();
        overFlowTimer.setStartedAt(afterMidnight);
        overFlowTimer.setStoppedAt(stoppedAt);
        overFlowTimer.setStatus(TimerType.STOPPED);

        // Duration for second part is also stored in seconds.
        overFlowTimer.setDurationInSeconds(
                Duration.between(afterMidnight, stoppedAt).getSeconds()
        );
        overFlowTimer.setWorkLog(workLogEntity);

        // Save second part and return both saved entities.
        TimerEntity secondSaved = timerRepository.save(overFlowTimer);
        return List.of(firstSaved, secondSaved);
    }

    private UserEntity getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }
        String username = auth.getName();
        return userRespository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found " + username));
    }

    private WorkLogEntity requireOwnedWorkLog(Long workLogId, Long ownerId) {
        return workLogRepository.findByIdAndOwnerId(workLogId, ownerId)
                .orElseThrow(() -> new AccessDeniedException("No access to worklog " + workLogId));
    }
}

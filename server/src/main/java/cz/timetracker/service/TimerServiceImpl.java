package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.dto.mapper.TimerMapper;
import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.enums.TimerType;
import cz.timetracker.entity.repository.TimerRepository;
import cz.timetracker.entity.repository.WorkLogRepository;
import cz.timetracker.service.exceptions.ConflictException;
import cz.timetracker.service.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimerServiceImpl implements TimerService{

    private final TimerRepository timerRepository;
    private final TimerMapper timerMapper;
    private final WorkLogRepository workLogRepository;

    public TimerServiceImpl(TimerRepository timerRepository,
                            TimerMapper timerMapper,
                            WorkLogRepository workLogRepository){
        this.timerRepository = timerRepository;
        this.timerMapper = timerMapper;
        this.workLogRepository = workLogRepository;
    }

    @Transactional
    @Override
    public TimerDTO startTimer(Long workLogId) {
        WorkLogEntity workLogEntity = workLogRepository.findById(workLogId)
                .orElseThrow(
                        () -> new NotFoundException("Work log with " + workLogId + "not found")
                );

        if(timerRepository.existsByWorkLogAndStatus(workLogId, TimerType.RUNNING)){
            throw new ConflictException("Timer already running for this worklog");
        }

        TimerEntity timerEntity = new TimerEntity();
        timerEntity.setStartedAt(LocalDateTime.now());
        timerEntity.setStatus(TimerType.RUNNING);
        timerEntity.setWorkLog(workLogEntity);
        TimerEntity saved = timerRepository.save(timerEntity);
        return timerMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public TimerDTO stopTimer(Long workLogId) {
        WorkLogEntity workLogEntity = workLogRepository.findById(workLogId)
                .orElseThrow(
                        () -> new NotFoundException("Work log with " + workLogId + "not found")
                );
        TimerEntity runningTimer = timerRepository
                .findFirstByWorkLogIdAndStatusOrderByStartedAtDesc(workLogId, TimerType.RUNNING)
                .orElseThrow(()-> new ConflictException("Timer is not running for this worklog " + workLogId));
        LocalDateTime stoppedAt = LocalDateTime.now();
        List<TimerEntity> savedTimers = stopAndSplitTimerIfNeeded(
                runningTimer, stoppedAt, workLogEntity
        );
        TimerEntity lastSaved = savedTimers.get(savedTimers.size() - 1);
        return  timerMapper.toDTO(lastSaved);
    }

    @Transactional
    private List<TimerEntity> stopAndSplitTimerIfNeeded(TimerEntity runningTimer,
                                                        LocalDateTime stoppedAt,
                                                        WorkLogEntity workLogEntity){
        LocalDate startDate = runningTimer.getStartedAt().toLocalDate();
        LocalDate stopDate = stoppedAt.toLocalDate();

        if(startDate.equals(stopDate)){
            runningTimer.setStoppedAt(stoppedAt);
            runningTimer.setStatus(TimerType.STOPPED);
            runningTimer.setDurationInSeconds(Duration.between(runningTimer.getStartedAt(),
                    stoppedAt).getSeconds());
            runningTimer.setWorkLog(workLogEntity);
            return  List.of(timerRepository.save(runningTimer));
        }

        LocalDateTime afterMidnight = startDate
                .plusDays(1).atStartOfDay();
        runningTimer.setStoppedAt(afterMidnight);
        runningTimer.setStatus(TimerType.STOPPED);
        runningTimer.setDurationInSeconds(Duration.between(runningTimer.getStartedAt(),
                afterMidnight).getSeconds());
        runningTimer.setWorkLog(workLogEntity);
        TimerEntity firstSaved = timerRepository.save(runningTimer);

        TimerEntity overFlowTimer = new TimerEntity();
        overFlowTimer.setStartedAt(afterMidnight);
        overFlowTimer.setStoppedAt(stoppedAt);
        overFlowTimer.setStatus(TimerType.STOPPED);
        overFlowTimer.setDurationInSeconds(
                Duration.between(afterMidnight, stoppedAt).getSeconds()
        );
        overFlowTimer.setWorkLog(workLogEntity);

        TimerEntity secondSaved = timerRepository.save(overFlowTimer);
        return List.of(firstSaved, secondSaved);
    }


}

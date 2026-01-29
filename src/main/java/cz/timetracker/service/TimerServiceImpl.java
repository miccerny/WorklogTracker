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
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

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

        if(timerRepository.existByWorkLogIdAndStatus(workLogId, TimerType.RUNNING)){
            throw new ConflictException("Timer already running for this worklog");
        }

        TimerEntity timerEntity = new TimerEntity();
        timerEntity.setStartedAt(LocalDateTime.now());
        timerEntity.setStatus(TimerType.RUNNING);
        timerEntity.setWorkLog(workLogEntity);
        TimerEntity saved = timerRepository.save(timerEntity);
        return timerMapper.toDTO(saved);
    }

    @Override
    public TimerDTO stopTimer(Long workLogId) {
        WorkLogEntity workLogEntity = workLogRepository.findById(workLogId)
                .orElseThrow(
                        () -> new NotFoundException("Work log with " + workLogId + "not found")
                );
        TimerEntity runningTimer = timerRepository
                .findFirstByWorkLogIdAndStatusOrderByStartedAtDesc(workLogId, TimerType.RUNNING)
                .orElseThrow(()-> new ConflictException("Timer is not running for this worklog " + workLogId));
        runningTimer.setStoppedAt(LocalDateTime.now());
        runningTimer.setStatus(TimerType.STOPPED);
        runningTimer.setWorkLog(workLogEntity);
        TimerEntity saved = timerRepository.save(runningTimer);
        return timerMapper.toDTO(saved);
    }


}

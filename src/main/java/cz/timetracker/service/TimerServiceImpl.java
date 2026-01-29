package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.dto.mapper.TimerMapper;
import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.enums.TimerType;
import cz.timetracker.entity.repository.TimerRepository;
import cz.timetracker.service.exceptions.ConflictException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimerServiceImpl implements TimerService{

    private final TimerRepository timerRepository;
    private final TimerMapper timerMapper;

    public TimerServiceImpl(TimerRepository timerRepository,
                            TimerMapper timerMapper){
        this.timerRepository = timerRepository;
        this.timerMapper = timerMapper;
    }

    @Override
    public TimerDTO startTimer(Long workLogId) {
        if(timerRepository.existByWorkLogIdAndStatus(workLogId, TimerType.RUNNING)){
            throw new ConflictException("Timer already running for this worklog");
        }

        TimerEntity timerEntity = new TimerEntity();
        timerEntity.setStartedAt(Instant.now());
        timerEntity.setStatus(TimerType.RUNNING);
        timerEntity.setWorkLog();
        
    }
}

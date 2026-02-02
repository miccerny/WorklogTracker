package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.dto.mapper.TimerMapper;
import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.repository.TimerRepository;
import org.springframework.stereotype.Service;

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
    public TimerDTO runTimer(TimerDTO timerDTO) {
        TimerEntity timerEntity = timerMapper.toEntity(timerDTO);
        
    }
}

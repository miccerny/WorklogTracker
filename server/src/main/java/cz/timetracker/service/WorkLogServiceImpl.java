package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.dto.mapper.WorkLogMapper;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.repository.WorkLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;

    private final WorkLogMapper workLogMapper;

    public WorkLogServiceImpl(WorkLogRepository workLogRepository,
                              WorkLogMapper workLogMapper){
        this.workLogRepository = workLogRepository;
        this.workLogMapper = workLogMapper;
    }

    @Transactional
    @Override
    public WorkLogDTO addWorkLog(WorkLogDTO projectTimer) {
        WorkLogEntity workLogEntity = workLogMapper.toEntity(projectTimer);
        WorkLogEntity savedEntity = workLogRepository.save(workLogEntity);
        return workLogMapper.toDTO(savedEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<WorkLogDTO> getAllWorkLogs() {
        return StreamSupport.stream(workLogRepository.findAll().spliterator(),
                false).map(i -> workLogMapper.toDTO(i))
                .toList();
    }

    @Transactional
    @Override
    public WorkLogDTO updateWorkLog(WorkLogDTO workLogDTO, Long id) {
        WorkLogEntity existing = workLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project " + id + "nenalezen"));
        existing.setActivated(false);
        workLogRepository.save(existing);

        WorkLogEntity updated = workLogMapper.toEntity(workLogDTO);
        WorkLogEntity saved = workLogRepository.save(updated);
        System.out.println(saved);
        return workLogMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public void deleteWorkLog(Long id){
        WorkLogEntity existing = workLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project " + id + "nenalezen"));
        workLogRepository.delete(existing);
    }
}

package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.dto.mapper.WorkLogMapper;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.repository.WorkLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Simple service layer for work logs.
 * <p>
 * This class talks to the repository and mapper so we can keep controllers clean.
 * The comments are written in a beginner-friendly style to explain each step.
 * </p>
 */
@Service
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;

    private final WorkLogMapper workLogMapper;

    /**
     * Creates the service with required dependencies.
     *
     * @param workLogRepository repository used for database access
     * @param workLogMapper mapper used to convert between entity and DTO
     */
    public WorkLogServiceImpl(WorkLogRepository workLogRepository,
                              WorkLogMapper workLogMapper) {
        this.workLogRepository = workLogRepository;
        this.workLogMapper = workLogMapper;
    }

    /**
     * Saves a new work log to the database.
     *
     * @param projectTimer work log data from the API layer
     * @return saved work log mapped back to a DTO
     */
    @Transactional
    @Override
    public WorkLogDTO addWorkLog(WorkLogDTO projectTimer) {
        WorkLogEntity workLogEntity = workLogMapper.toEntity(projectTimer);
        WorkLogEntity savedEntity = workLogRepository.save(workLogEntity);
        return workLogMapper.toDTO(savedEntity);
    }

    /**
     * Returns all work logs from the database.
     *
     * @return list of work log DTOs
     */
    @Transactional(readOnly = true)
    @Override
    public List<WorkLogDTO> getAllWorkLogs() {
        return StreamSupport.stream(workLogRepository.findAll().spliterator(),
                        false).map(i -> workLogMapper.toDTO(i))
                .toList();
    }

    /**
     * Updates a work log by first deactivating the old one and then saving the new one.
     *
     * @param workLogDTO new work log data
     * @param id id of the work log to replace
     * @return updated work log DTO
     */
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

    /**
     * Deletes a work log by id.
     *
     * @param id id of the work log to remove
     */
    @Transactional
    @Override
    public void deleteWorkLog(Long id) {
        WorkLogEntity existing = workLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project " + id + "nenalezen"));
        workLogRepository.delete(existing);
    }
}

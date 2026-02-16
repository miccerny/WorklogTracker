package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.dto.mapper.WorkLogMapper;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.entity.repository.WorkLogRepository;
import cz.timetracker.service.exceptions.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Implementation of WorkLogService.
 *
 * This class contains business logic for:
 * - creating WorkLogs
 * - retrieving WorkLogs
 * - deleting WorkLogs
 *
 * It coordinates repository layer and mapper.
 */
@Service
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;

    private final WorkLogMapper workLogMapper;

    private final UserRespository userRespository;

    /**
     * Constructor injection of dependencies.
     *
     * Spring automatically injects repository and mapper.
     *
     * @param workLogRepository repository for DB operations
     * @param workLogMapper mapper for converting entity <-> DTO
     */
    public WorkLogServiceImpl(WorkLogRepository workLogRepository,
                              WorkLogMapper workLogMapper,
                              UserRespository userRespository) {
        this.workLogRepository = workLogRepository;
        this.workLogMapper = workLogMapper;
        this.userRespository = userRespository;
    }

    /**
     * Creates new WorkLog.
     *
     * Flow:
     * 1. Map DTO to entity
     * 2. Set creation timestamp
     * 3. Save to DB
     * 4. Return mapped DTO
     */
    @Transactional
    @Override
    public WorkLogDTO createWorkLog(WorkLogDTO projectTimer) {
        UserEntity user = getCurrentUser();
        // Convert DTO (incoming request) to entity.
        WorkLogEntity workLogEntity = workLogMapper.toEntity(projectTimer);

        // Persist entity into database.
        workLogEntity.setOwner(user);
        WorkLogEntity savedEntity = workLogRepository.save(workLogEntity);

        // Convert saved entity back to DTO and return.
        return workLogMapper.toDTO(savedEntity);
    }

    /**
     * Returns all WorkLogs for given user.
     *
     * @return list of WorkLogDTO
     */
    @Transactional(readOnly = true)
    @Override
    public List<WorkLogDTO> getAllWorkLogs() {
        UserEntity user = getCurrentUser();

        // Load entities from DB filtered by user ID.
        return workLogRepository.findByOwnerId(user.getId())
                .stream()
                .map(workLogMapper::toDTO)
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
        UserEntity entity = getCurrentUser();
        WorkLogEntity existing = workLogRepository.findByIdAndOwnerId(id, entity.getId())
                .orElseThrow(() -> new RuntimeException("Project " + id + "nenalezen"));
        existing.setActivated(false);
        workLogRepository.save(existing);

        WorkLogEntity updated = workLogMapper.toEntity(workLogDTO);
        WorkLogEntity saved = workLogRepository.save(updated);
        System.out.println(saved);
        return workLogMapper.toDTO(saved);
    }

    /**
     * Deletes WorkLog by ID.
     *
     * @param id WorkLog ID
     * @throws NotFoundException if WorkLog does not exist
     */
    @Transactional
    @Override
    public void deleteWorkLog(Long id) {
            // Validate existence before delete (better error message)

            // Perform deletion.
            workLogRepository.delete(existing(id));
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

    private WorkLogEntity existing(Long id){
        UserEntity userEntity = getCurrentUser();
        return workLogRepository.findByIdAndOwnerId(id, userEntity.getId())
                .orElseThrow(() -> new NotFoundException("Worklog not found: " + id));

    }
}

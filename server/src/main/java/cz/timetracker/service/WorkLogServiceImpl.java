package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.dto.mapper.WorkLogMapper;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.entity.repository.WorkLogRepository;
import cz.timetracker.service.exceptions.ForbiddenException;
import cz.timetracker.service.exceptions.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Implementation of WorkLogService.
 * <p>
 * This class contains business logic for:
 * - creating WorkLogs
 * - retrieving WorkLogs
 * - deleting WorkLogs
 * <p>
 * It coordinates repository layer and mapper.
 */
@Service
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;

    private final WorkLogMapper workLogMapper;

    private final UserRespository userRespository;

    /**
     * Constructor injection of dependencies.
     * <p>
     * Spring automatically injects repository and mapper.
     *
     * @param workLogRepository repository for DB operations
     * @param workLogMapper     mapper for converting entity <-> DTO
     */
    public WorkLogServiceImpl(WorkLogRepository workLogRepository,
                              WorkLogMapper workLogMapper,
                              UserRespository userRespository) {
        this.workLogRepository = workLogRepository;
        this.workLogMapper = workLogMapper;
        this.userRespository = userRespository;
    }

    /**
     * Creates a new WorkLog for the currently authenticated user.
     *
     * <p>Flow:</p>
     * <ol>
     *     <li>Load the current user from the security context</li>
     *     <li>Map DTO to entity</li>
     *     <li>Assign owner</li>
     *     <li>Save entity</li>
     *     <li>Map saved entity back to DTO</li>
     * </ol>
     *
     * @param projectTimer incoming WorkLog data
     * @return created WorkLog as {@link WorkLogDTO}
     */
    @Transactional
    @Override
    public WorkLogDTO createWorkLog(WorkLogDTO projectTimer) {
        // Load current user (authentication must exist).
        UserEntity user = getCurrentUser();
        // Convert DTO (incoming request) to entity.
        WorkLogEntity workLogEntity = workLogMapper.toEntity(projectTimer);

        // Assign the owner so WorkLog is linked to the logged-in user.
        workLogEntity.setOwner(user);

        // Persist entity into database.
        WorkLogEntity savedEntity = workLogRepository.save(workLogEntity);

        // Convert saved entity back to DTO and return.
        return workLogMapper.toDTO(savedEntity);
    }

    /**
     * Returns all WorkLogs for the currently authenticated user.
     *
     * <p><b>Note:</b>
     * We filter by owner ID so users only see their own data.</p>
     *
     * @return list of {@link WorkLogDTO} belonging to the current user
     */
    @Transactional(readOnly = true)
    @Override
    public List<WorkLogDTO> getAllWorkLogs() {
        // Load current user (authentication must exist).
        UserEntity user = getCurrentUser();

        // Load entities from DB filtered by user ID and map them to DTOs.
        return workLogRepository.findByOwnerId(user.getId())
                .stream()
                .map(workLogMapper::toDTO)
                .toList();
    }

    /**
     * Updates a WorkLog by deactivating the old record and creating a new one.
     *
     * <p><b>Note:</b>
     * This method behaves more like "replace" than a typical update:
     * it disables the old WorkLog and creates a new WorkLog row.</p>
     *
     * @param workLogDTO new WorkLog data
     * @param id         ID of the WorkLog to replace
     * @return newly created WorkLog DTO (replacement)
     */
    @Transactional
    @Override
    public WorkLogDTO updateWorkLog(WorkLogDTO workLogDTO, Long id) {
        // Load current user (authentication must exist).
        UserEntity user = getCurrentUser();

        // Ensure the WorkLog exists and belongs to the current user
        WorkLogEntity old = existing(id);

        // Deactivate the old WorkLog.
        old.setActivated(false);
        workLogRepository.save(old);

        // Create a new entity from DTO.
        WorkLogEntity updated = workLogMapper.toEntity(workLogDTO);
        // Ensure a new database row is created (ID is generated).
        updated.setId(null);
        // Assign owner to the new WorkLog.
        updated.setOwner(user);
        // Persist the new WorkLog
        WorkLogEntity saved = workLogRepository.save(updated);
        // Return the saved WorkLog as DTO.
        return workLogMapper.toDTO(saved);
    }

    /**
     * Deletes a WorkLog by ID (only if it belongs to the current user).
     *
     * <p><b>Note:</b>
     * We load the entity via {@link #existing(Long)} first, which also
     * acts as an authorization check.</p>
     *
     * @param id WorkLog ID
     */
    @Transactional
    @Override
    public void deleteWorkLog(Long id) {

        // Load the WorkLog (throws exception if not found / not owned) and Perform deletion.
        workLogRepository.delete(existing(id));
    }

    /**
     * Returns the currently authenticated user.
     *
     * <p>This method reads the Spring Security context and loads the user entity
     * from the database.</p>
     *
     * <p><b>Beginner note:</b>
     * Authentication is set by Spring Security (e.g., JWT filter) before
     * this service method is called.</p>
     *
     * @return current {@link UserEntity}
     * @throws AccessDeniedException if no authenticated user exists
     * @throws NotFoundException if the user from security context cannot be found in DB
     */
    private UserEntity getCurrentUser() {

        // Read current authentication from SecurityContext.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If user is not authenticated, reject access.
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        // In this project, username is stored as authentication name (email).
        String username = auth.getName();

        // Load the user entity from database.
        return userRespository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found " + username));
    }

    /**
     * Loads an existing WorkLog by ID that belongs to the current user.
     *
     * <p><b>Beginner note:</b>
     * This method acts as both:
     * <ul>
     *     <li>existence check (WorkLog must exist)</li>
     *     <li>authorization check (WorkLog must belong to the user)</li>
     * </ul>
     * </p>
     *
     * @param id WorkLog ID
     * @return {@link WorkLogEntity} owned by current user
     * @throws ForbiddenException if the WorkLog does not belong to the user (or is not found via owner filter)
     */
    private WorkLogEntity existing(Long id) {
        // Load current user (authentication must exist).
        UserEntity userEntity = getCurrentUser();

        // Find WorkLog only within current user's ownership scope.
        return workLogRepository.findByIdAndOwnerId(id, userEntity.getId())
                .orElseThrow(() -> new ForbiddenException("Worklog not found: " + id));
    }
}

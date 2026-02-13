package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;

import java.util.List;

/**
 * Service interface defining business operations
 * related to WorkLog management.
 *
 * This layer separates controller (HTTP layer)
 * from business logic implementation.
 *
 * Implementation is provided in WorkLogServiceImpl.
 */
public interface WorkLogService {

    /**
     * Creates a new WorkLog.
     *
     * Expected behavior:
     * - Validate input data
     * - Persist new WorkLog entity
     * - Return created WorkLog as DTO
     *
     * @param projectTimer data for new WorkLog
     * @return created WorkLog
     */
    WorkLogDTO createWorkLog(WorkLogDTO projectTimer);

    /**
     * Returns all WorkLogs for given user.
     *
     * Typically used when user opens dashboard
     * and wants to see list of projects.
     *
     *
     * @return list of WorkLogs
     */
    List<WorkLogDTO> getAllWorkLogs();

    /**
     * Returns single WorkLog by ID.
     *
     * @param id WorkLog ID
     * @return WorkLog DTO
     */
    WorkLogDTO updateWorkLog(WorkLogDTO workLogDTO, Long id);

    /**
     * Deletes WorkLog by ID.
     *
     * Usually includes validation:
     * - WorkLog must exist
     * - Possibly check ownership
     *
     * @param id WorkLog ID
     */
    void deleteWorkLog(Long id);
}

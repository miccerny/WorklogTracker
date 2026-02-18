package cz.timetracker.controller;


import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.service.WorkLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for managing WorkLogs.
 *
 * <p>This controller exposes CRUD operations for WorkLog resources:</p>
 * <ul>
 *     <li>Create new WorkLog</li>
 *     <li>Retrieve all WorkLogs</li>
 *     <li>Update existing WorkLog</li>
 *     <li>Delete WorkLog</li>
 * </ul>
 *
 * <p><b>Beginner note:</b>
 * The controller should remain "thin". It only handles HTTP requests
 * and delegates all business logic to the {@link WorkLogService}.</p>
 */
@RequestMapping("/api")
@RestController
public class WorkLogController {

    private final WorkLogService workLogService;

    /**
     * Constructor injection of WorkLogService.
     *
     * @param workLogService service responsible for business logic
     */
    public WorkLogController(WorkLogService workLogService){
        this.workLogService = workLogService;
    }

    /**
     * Creates a new WorkLog.
     *
     * <p>The request body contains WorkLog data that will be persisted
     * by the service layer.</p>
     *
     * @param workLogDTO incoming WorkLog data
     * @return created {@link WorkLogDTO}
     */
    @PostMapping("/worklogs")
    public WorkLogDTO addProject(@RequestBody WorkLogDTO workLogDTO){
        return workLogService.createWorkLog(workLogDTO);
    }

    /**
     * Retrieves all WorkLogs.
     *
     * <p><b>Beginner note:</b>
     * Filtering (e.g., by current user) should be handled
     * inside the service layer.</p>
     *
     * @return list of {@link WorkLogDTO}
     */
    @GetMapping("/worklogs")
    public List<WorkLogDTO> getAllProjects(){
        return workLogService.getAllWorkLogs();
    }


    /**
     * Updates an existing WorkLog.
     *
     * <p>The ID of the WorkLog is provided as a request parameter,
     * while updated data is provided in the request body.</p>
     *
     * @param workLogDTO updated WorkLog data
     * @param id ID of the WorkLog to update
     * @return updated {@link WorkLogDTO}
     */
    @PutMapping("/worklogs")
    public WorkLogDTO updateProject(@RequestBody WorkLogDTO workLogDTO, @RequestParam Long id){
        return workLogService.updateWorkLog(workLogDTO, id);
    }

    /**
     * Deletes a WorkLog.
     *
     * <p><b>Beginner note:</b>
     * The service layer should validate whether the WorkLog exists
     * and whether the current user is allowed to delete it.</p>
     *
     * @param projectId ID of the WorkLog to delete
     */
    @DeleteMapping("/worklogs")
    public void removeProject(@RequestParam Long projectId){
        workLogService.deleteWorkLog(projectId);
    }
}

package cz.timetracker.controller;


import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.dto.user.LoginRequest;
import cz.timetracker.dto.user.LoginResponse;
import cz.timetracker.service.WorkLogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService){
        this.workLogService = workLogService;
    }

    @PostMapping("/worklogs")
    public WorkLogDTO addProject(@RequestBody WorkLogDTO workLogDTO){
        return workLogService.createWorkLog(workLogDTO);
    }

    @GetMapping("/worklogs")
    public List<WorkLogDTO> getAllProjects(){
        return workLogService.getAllWorkLogs();
    }



    @PutMapping("/worklogs")
    public WorkLogDTO updateProject(@RequestBody WorkLogDTO workLogDTO, @RequestParam Long id){
        return workLogService.updateWorkLog(workLogDTO, id);
    }

    @DeleteMapping("/worklogs")
    public void removeProject(@RequestParam Long projectId){
        workLogService.deleteWorkLog(projectId);
    }
}

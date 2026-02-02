package cz.timetracker.controller;


import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.service.WorkLogService;
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
        return workLogService.addProject(workLogDTO);
    }

    @GetMapping("/worklogs")
    public List<WorkLogDTO> getAllProjects(){
        return workLogService.getAllProjects();
    }



    @PutMapping("/worklogs")
    public WorkLogDTO updateProject(@RequestBody WorkLogDTO workLogDTO, @RequestParam Long id){
        return workLogService.updateProject(workLogDTO, id);
    }

    @DeleteMapping("/worklogs")
    public void removeProject(@RequestParam Long projectId){
        workLogService.deleteProject(projectId);
    }
}

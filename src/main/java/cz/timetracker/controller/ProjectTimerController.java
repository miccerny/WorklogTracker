package cz.timetracker.controller;


import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.service.ProjectTimerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class ProjectTimerController {

    private final ProjectTimerService projectTimerService;

    public ProjectTimerController(ProjectTimerService projectTimerService){
        this.projectTimerService = projectTimerService;
    }

    @PostMapping("/project")
    public ProjectTimerDTO addProject( @RequestBody ProjectTimerDTO projectTimerDTO){
        return projectTimerService.addProject(projectTimerDTO);
    }

    @GetMapping("/project")
    public List<ProjectTimerDTO> getAllProjects(){
        return projectTimerService.getAllProjects();
    }
}

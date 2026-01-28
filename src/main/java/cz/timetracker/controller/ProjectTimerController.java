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

    @GetMapping("/project/timers")
    public ProjectTimerDTO getProject(@RequestParam Long projectId){
        return projectTimerService.getProject(projectId);
    }

    @PutMapping("/project")
    public ProjectTimerDTO updateProject( ProjectTimerDTO projectTimerDTO, @RequestParam Long projectId){
        return projectTimerService.updateProject(projectTimerDTO, projectId);
    }

    @DeleteMapping("/project/timers")
    public void removeProject(@RequestParam Long projectId){
        projectTimerService.deleteProject(projectId);
    }
}

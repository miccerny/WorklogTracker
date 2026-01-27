package cz.timetracker.service;

import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.entity.ProjectTimerEntity;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ProjectTimerService {

    ProjectTimerDTO addProject(ProjectTimerDTO projectTimer);
    List<ProjectTimerDTO> getAllProjects();

    void deleteProject(Long id);
}

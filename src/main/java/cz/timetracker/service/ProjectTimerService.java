package cz.timetracker.service;

import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.entity.ProjectTimerEntity;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ProjectTimerService {

    ProjectTimerDTO addProject(ProjectTimerDTO projectTimer);
    List<ProjectTimerDTO> getAllProjects();
    ProjectTimerDTO getProject(Long id);
    ProjectTimerDTO updateProject(ProjectTimerDTO projectTimerDTO, Long id);
    void deleteProject(Long id);
}

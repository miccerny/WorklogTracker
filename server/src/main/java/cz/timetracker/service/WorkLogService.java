package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;

import java.util.List;

public interface WorkLogService {

    WorkLogDTO addProject(WorkLogDTO projectTimer);
    List<WorkLogDTO> getAllProjects();
    WorkLogDTO getProject(Long id);
    WorkLogDTO updateProject(WorkLogDTO workLogDTO, Long id);
    void deleteProject(Long id);
}

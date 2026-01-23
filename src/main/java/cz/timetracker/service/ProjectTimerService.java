package cz.timetracker.service;

import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.entity.ProjectTimerEntity;

public interface ProjectTimerService {

    ProjectTimerDTO addProject(ProjectTimerDTO projectTimer);

}

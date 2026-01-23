package cz.timetracker.service;

import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.dto.mapper.ProjectTimerMapper;
import cz.timetracker.entity.ProjectTimerEntity;
import cz.timetracker.entity.repository.ProjectTimerRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectTimerServiceImpl implements ProjectTimerService{

    private ProjectTimerRepository projectTimerRepository;

    private ProjectTimerMapper projectTimerMapper;

    public ProjectTimerRepository getProjectTimerRepository() {
        return projectTimerRepository;
    }

    public ProjectTimerMapper getProjectTimerMapper() {
        return projectTimerMapper;
    }

    @Override
    public ProjectTimerDTO addProject(ProjectTimerDTO projectTimer) {
        ProjectTimerEntity projectTimerEntity = projectTimerMapper.toEntity(projectTimer);
        ProjectTimerEntity savedEntity = projectTimerRepository.save(projectTimerEntity);
        return projectTimerMapper.toDTO(savedEntity);
    }
}

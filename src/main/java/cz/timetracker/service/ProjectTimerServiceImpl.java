package cz.timetracker.service;

import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.dto.mapper.ProjectTimerMapper;
import cz.timetracker.entity.ProjectTimerEntity;
import cz.timetracker.entity.repository.ProjectTimerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class ProjectTimerServiceImpl implements ProjectTimerService{

    private final ProjectTimerRepository projectTimerRepository;

    private final ProjectTimerMapper projectTimerMapper;

    public ProjectTimerServiceImpl(ProjectTimerRepository projectTimerRepository,
                                   ProjectTimerMapper projectTimerMapper){
        this.projectTimerRepository = projectTimerRepository;
        this.projectTimerMapper = projectTimerMapper;
    }

    @Transactional
    @Override
    public ProjectTimerDTO addProject(ProjectTimerDTO projectTimer) {
        ProjectTimerEntity projectTimerEntity = projectTimerMapper.toEntity(projectTimer);
        ProjectTimerEntity savedEntity = projectTimerRepository.save(projectTimerEntity);
        return projectTimerMapper.toDTO(savedEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProjectTimerDTO> getAllProjects() {
        return StreamSupport.stream(projectTimerRepository.findAll().spliterator(),
                false).map(i -> projectTimerMapper.toDTO(i))
                .toList();
    }

    @Transactional
    @Override
    public void deleteProject(Long id){
        ProjectTimerEntity existing = projectTimerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project " + id + "nenalezen"));
        projectTimerRepository.delete(existing);
    }
}

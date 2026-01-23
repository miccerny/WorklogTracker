package cz.timetracker.dto.mapper;

import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.entity.ProjectTimerEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ProjectTimerMapper {

    ProjectTimerDTO toDTO (ProjectTimerEntity source);
    ProjectTimerEntity toEntity(ProjectTimerDTO source);

}

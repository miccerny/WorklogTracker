package cz.timetracker.dto.mapper;

import cz.timetracker.dto.ProjectTimerDTO;
import cz.timetracker.entity.ProjectTimerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectTimerMapper {

    ProjectTimerDTO toDTO (ProjectTimerEntity source);

    ProjectTimerEntity toEntity(ProjectTimerDTO source);

}

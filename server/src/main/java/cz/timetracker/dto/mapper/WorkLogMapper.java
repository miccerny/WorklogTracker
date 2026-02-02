package cz.timetracker.dto.mapper;

import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.entity.WorkLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkLogMapper {

    WorkLogDTO toDTO (WorkLogEntity source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activated", constant = "true")
    WorkLogEntity toEntity(WorkLogDTO source);

}

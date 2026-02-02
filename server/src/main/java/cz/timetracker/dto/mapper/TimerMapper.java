package cz.timetracker.dto.mapper;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.entity.TimerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimerMapper {

    @Mapping(target = "createdAt", source = "startedAt")
    TimerDTO toDTO(TimerEntity source);
}

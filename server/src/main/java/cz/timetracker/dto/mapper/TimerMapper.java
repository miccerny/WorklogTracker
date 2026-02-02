package cz.timetracker.dto.mapper;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.entity.TimerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimerMapper {

    TimerEntity toEntity(TimerDTO source);
    TimerDTO toDTO(TimerEntity source);
}

package cz.timetracker.dto.mapper;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.entity.TimerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for converting {@link TimerEntity}
 * to {@link TimerDTO}.
 *
 * <p>This mapper uses MapStruct to automatically generate
 * the implementation at compile time.</p>
 *
 * <p><b>Beginner note:</b>
 * MapStruct generates the mapping code for us, so we do not
 * need to manually write conversion logic.</p>
 */
@Mapper(componentModel = "spring")
public interface TimerMapper {

    /**
     * Converts {@link TimerEntity} to {@link TimerDTO}.
     *
     * <p>The field {@code startedAt} from the entity is mapped
     * to {@code createdAt} in the DTO.</p>
     *
     * <p><b>note:</b>
     * The {@code @Mapping} annotation is needed because the field names
     * differ between entity and DTO. If they were the same,
     * MapStruct would map them automatically.</p>
     *
     * @param source entity object loaded from the database
     * @return mapped DTO object
     */
    @Mapping(target = "createdAt", source = "startedAt")
    TimerDTO toDTO(TimerEntity source);
}

package cz.timetracker.dto.mapper;

import cz.timetracker.dto.user.UserResponse;
import cz.timetracker.entity.UserEntity;
import org.mapstruct.Mapper;

/**
 * Mapper interface responsible for converting {@link UserEntity}
 * to {@link UserResponse}.
 *
 * <p>This mapper uses MapStruct to generate the implementation
 * automatically at compile time.</p>
 *
 * <p><b>Beginner note:</b>
 * MapStruct eliminates the need to manually copy fields
 * from entity to DTO, reducing boilerplate code and
 * minimizing the risk of mapping errors.</p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a {@link UserEntity} to a {@link UserResponse}.
     *
     * <p>If field names are identical between entity and DTO,
     * MapStruct maps them automatically without additional configuration.</p>
     *
     * @param source entity loaded from the database
     * @return mapped DTO used in API responses
     */
    UserResponse toDTO(UserEntity source);
}

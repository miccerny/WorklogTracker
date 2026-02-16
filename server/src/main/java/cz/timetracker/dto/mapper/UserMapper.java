package cz.timetracker.dto.mapper;

import cz.timetracker.dto.user.UserRegistryRequest;
import cz.timetracker.dto.user.UserResponse;
import cz.timetracker.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserEntity toEntity(UserRegistryRequest source);

    UserResponse toDTO(UserEntity source);
}

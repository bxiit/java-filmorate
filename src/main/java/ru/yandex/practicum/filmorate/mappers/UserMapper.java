package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    UserDto mapToUserDto(User user);

    User toUser(UserDto userDto);

    User mapNewUserToUser(NewUserRequest request);


    @Mapping(ignore = true, target = "id")
//    @Mapping(target = "email", source = "request.email", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "name", source = "request.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "login", source = "request.login", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "birthday", source = "request.birthday", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFields(@MappingTarget User user, UpdateUserRequest request);

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);
}

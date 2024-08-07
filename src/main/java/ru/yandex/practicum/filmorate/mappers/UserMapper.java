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

@Mapper(builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "friends", expression = "java(user.getFriends())")
    UserDto mapToUserDto(User user);

    @Mapping(target = "friends", expression = "java(new java.util.HashSet<>())")
    User toUser(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "friends", expression = "java(new java.util.HashSet<>())")
    User mapNewUserToUser(NewUserRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "friends", ignore = true)
    @Mapping(target = "email", source = "request.email", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", source = "request.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "login", source = "request.login", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "birthday", source = "request.birthday", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFields(@MappingTarget User user, UpdateUserRequest request);

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);
}

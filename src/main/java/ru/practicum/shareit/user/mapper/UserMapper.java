package ru.practicum.shareit.user.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    User mapToUser(UserDto userDto);

    UserDto mapToUserDto(User user);

    List<UserDto> mapToListDto(List<User> userList);
}

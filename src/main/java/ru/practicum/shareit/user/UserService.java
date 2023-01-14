package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto getUserById(Long userId);

    UserDto updateUser(UserDto userDto, Long id);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers();
}

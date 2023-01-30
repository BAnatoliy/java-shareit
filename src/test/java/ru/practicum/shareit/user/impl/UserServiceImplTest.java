package ru.practicum.shareit.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapperImpl userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UserName");
        userDto.setEmail("u@ya.ru");
        User user = new User();
        user.setId(1L);
        user.setName("UserName");
        user.setEmail("u@ya.ru");

        when(userMapper.mapToUser(userDto)).thenReturn(user);
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);
        when(userRepository.save(user)).thenReturn(user);

        UserDto userDtoReturn = userService.createUser(userDto);
        assertEquals(userDto, userDtoReturn);
        verify(userRepository).save(user);
        verify(userMapper).mapToUserDto(user);
    }

    @Test
    void getUserByIdTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UserName");
        userDto.setEmail("u@ya.ru");
        User user = new User();
        user.setId(1L);
        user.setName("UserName");
        user.setEmail("u@ya.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        UserDto userReturn = userService.getUserById(1L);
        assertEquals(userDto, userReturn);
        verify(userRepository).findById(1L);
        verify(userMapper).mapToUserDto(user);
    }

    @Test
    void getUserByIdTest_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUserById(1L));
    }

    @Test
    void getAllUsers() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UserName");
        userDto.setEmail("u@ya.ru");
        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("UserName2");
        userDto2.setEmail("u2@ya.ru");
        User user = new User();
        user.setId(1L);
        user.setName("UserName");
        user.setEmail("u@ya.ru");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("UserName2");
        user2.setEmail("u2@ya.ru");

        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);
        when(userMapper.mapToUserDto(user2)).thenReturn(userDto2);

        List<UserDto> allUsers = userService.getAllUsers();
        assertEquals(List.of(userDto, userDto2), allUsers);
        verify(userRepository).findAll();
        verify(userMapper).mapToUserDto(user);
        verify(userMapper).mapToUserDto(user2);
    }

    @Test
    void updateUser_whenInvoked_shouldGetListUsers() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UserName");
        userDto.setEmail("u@ya.ru");

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("OldUserName");
        oldUser.setEmail("oldu@ya.ru");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("UserName");
        newUser.setEmail("u@ya.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userMapper.mapToUser(userDto)).thenReturn(oldUser);
        when(userMapper.mapToUserDto(any(User.class))).thenReturn(userDto);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserDto updateUser = userService.updateUser(userDto, 1L);
        assertEquals(userDto, updateUser);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(userMapper).mapToUser(userDto);
        verify(userMapper).mapToUserDto(any(User.class));
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowException() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UserName");
        userDto.setEmail("u@ya.ru");

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("OldUserName");
        oldUser.setEmail("oldu@ya.ru");

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(userMapper.mapToUser(userDto)).thenReturn(oldUser);

        assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser(userDto, 2L));

    }

    @Test
    void updateUser_whenUserEmailIsBlank_shouldThrowException() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UserName");
        userDto.setEmail("   ");

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("OldUserName");
        oldUser.setEmail("   ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userMapper.mapToUser(userDto)).thenReturn(oldUser);

        assertThrows(ValidationException.class, () ->
                userService.updateUser(userDto, 1L));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper).mapToUser(userDto);
        verify(userMapper, never()).mapToUserDto(any(User.class));
    }

    @Test
    void updateUser_whenUserNameIsBlank_shouldThrowException() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("   ");
        userDto.setEmail("u@ya.ru");

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("   ");
        oldUser.setEmail("oldu@ya.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userMapper.mapToUser(userDto)).thenReturn(oldUser);

        assertThrows(ValidationException.class, () ->
                userService.updateUser(userDto, 1L));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper).mapToUser(userDto);
        verify(userMapper, never()).mapToUserDto(any(User.class));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}
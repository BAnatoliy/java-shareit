package ru.practicum.shareit.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        log.debug("User created");
        return userMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong", userId)));
        log.debug("User with ID = {} is found", userId);
        return userMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        log.debug("Get user`s list");
        return userMapper.mapToListDto(userList);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userMapper.mapToUser(userDto);
        User oldUser = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.debug("User with ID = {} is found", userId);
                    return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                            userId));
                });
        String userEmail = user.getEmail();
        String userName = user.getName();

        if (userEmail != null) {
            if (userEmail.isBlank()) {
                throw new ValidationException("Email cannot be empty");
            }
            oldUser.setEmail(user.getEmail());
        }
        if (userName != null) {
            if (userName.isBlank()) {
                throw new ValidationException("Name cannot be empty");
            }
            oldUser.setName(user.getName());
        }
        User savedUser = userRepository.save(oldUser);
        log.debug("User updated");
        return userMapper.mapToUserDto(savedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.debug("User with ID = {} deleted", userId);
    }
}

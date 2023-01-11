package ru.practicum.shareit.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User createUser(User user) {
        userRepository.save(user);
        log.debug("User created");
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong", userId)));
        log.debug("User with ID = {} is found", userId);
        return user;
    }

    @Transactional
    @Override
    public User updateUser(User user, Long userId) {
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
        return savedUser;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.debug("User with ID = {} deleted", userId);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = userRepository.findAll();
        log.debug("Get user`s list");
        return userList;
    }
}

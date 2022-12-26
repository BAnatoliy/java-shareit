package ru.practicum.shareit.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    public User updateUser(Long userId, User user) {
        return userRepository.updateUser(userId, user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}

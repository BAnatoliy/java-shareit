package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    List<User> getAllUsers();
}

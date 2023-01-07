package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    User updateUser(User user, Long id);

    void deleteUser(Long userId);

    List<User> getAllUsers();
}

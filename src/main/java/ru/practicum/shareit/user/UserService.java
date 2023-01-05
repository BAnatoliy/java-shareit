package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    User updateUser(User user);

    void deleteUser(Long userId);

    List<User> getAllUsers();
}

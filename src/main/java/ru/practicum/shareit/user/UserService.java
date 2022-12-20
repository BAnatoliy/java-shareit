package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    public User updateUser(Long userId, User user) {
        //user.setId(id);
        return userRepository.updateUser(userId, user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}

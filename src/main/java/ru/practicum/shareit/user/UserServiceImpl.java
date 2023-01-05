package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public UserServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
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
    public User updateUser(User user) {
        User oldUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong", user.getId())));

        if (user.getEmail().isBlank() || user.getName().isBlank()) {
            throw new ValidationException("Email and name cannot be empty");
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }

        userRepository.save(oldUser);
        log.debug("User updated");
        return oldUser;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.debug("User with ID = {} deleted", userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Get user`s list");
        return userRepository.findAll();
    }
}

package ru.practicum.shareit.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
//@Slf4j
public class UserRepositoryInMemory {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, List<Long>> itemsByOwner = new HashMap<>();

    private long userId = 1;

    //@Override
    public User createUser(User user) {
        user.setId(userId);
        if (validEmailToExist(user)) {
            throw new ValidationException("This email has already exist");
        }
        if (users.containsValue(user)) {
            throw new ValidationException("This user has already created");
        }
        users.put(user.getId(), user);
        generatedId();
        return user;
    }

    //@Override
    public User updateUser(Long id, User user) {
        if (validEmailToExist(user)) {
            throw new ValidationException("This email has already exist");
        }
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("User not found");
        }
        User userForUpdate = users.get(id);
        String userName = user.getName();
        String userEmail = user.getEmail();
        if (userName != null) {
            userForUpdate.setName(userName);
        }
        if (userEmail != null) {
            userForUpdate.setEmail(userEmail);
        }
        users.put(id, userForUpdate);
        return userForUpdate;
    }

    //@Override
    public void deleteUser(Long id) {
        if (id == null || !users.containsKey(id)) {
            throw new EntityNotFoundException("User not found");
        }

        users.remove(id);
    }

    //@Override
    public User getUserById(Long id) {
        if (id == null || !users.containsKey(id)) {
            throw new EntityNotFoundException("User not found");
        }

        return users.get(id);
    }

    //@Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void addItemByOwner(Long ownerId, Long itemId) {
        if (ownerId == null || !users.containsKey(ownerId)) {
            throw new EntityNotFoundException("User not found");
        }
        if (itemsByOwner.containsKey(ownerId)) {
            itemsByOwner.get(ownerId).add(itemId);
        } else {
            itemsByOwner.put(ownerId, new ArrayList<>(List.of(itemId)));
        }
    }

    public List<Long> getItemsIdByTheOwner(Long ownerId) {
        if (!itemsByOwner.containsKey(ownerId)) {
            throw new EntityNotFoundException("User not found");
        }
        return itemsByOwner.get(ownerId);
    }

    private void generatedId() {
        userId++;
    }

    private boolean validEmailToExist(User user) {
        long count = users.values().stream().filter(u -> u.getEmail().equals(user.getEmail())).count();
        return count > 0;
    }
}
